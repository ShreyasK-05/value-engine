import yfinance as yf
from confluent_kafka import Producer
import json
import time
import logging
import concurrent.futures
import os

# Configure basic logging to monitor the daemon
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# 1. Kafka Configuration
# Pointing to the Kafka broker exposed by our Docker container
conf = {'bootstrap.servers': 'localhost:9092'}
producer = Producer(conf)

def delivery_report(err, msg):
    """Callback triggered upon successful or failed delivery of a message to Kafka."""
    if err is not None:
        logging.error(f"Message delivery failed: {err}")

def load_tickers(file_path="test_tickers.txt"):
    """Reads the external ticker file and returns a clean list of symbols."""
    tickers = []
    try:
        with open(file_path, 'r') as file:
            for line in file:
                clean_ticker = line.strip()
                if clean_ticker:
                    tickers.append(clean_ticker)
        return tickers
    except FileNotFoundError:
        logging.error(f"CRITICAL: {file_path} not found. Halting execution.")
        return []
    except Exception as e:
        logging.error(f"ERROR reading {file_path}: {e}")
        return []

def process_single_ticker(ticker):
    """Fetches data for a single company with built-in fault tolerance."""
    base_delay = 2
    max_delay = 32
    success = False
    attempts = 0

    while not success and attempts < 5:
        try:
            # Rate Limiting: Throttle requests to respect API limits within the thread
            time.sleep(1)

            # Fetch live data using yfinance
            stock = yf.Ticker(ticker)
            info = stock.info

            # Extract only the exact data we need for our Java modeling engine
            payload = {
                "ticker": ticker,
                "currentPrice": info.get("currentPrice", 0.0),
                "trailingPE": info.get("trailingPE", 0.0),
                "returnOnEquity": info.get("returnOnEquity", 0.0),
                "debtToEquity": info.get("debtToEquity", 0.0),
                "timestamp": time.time()
            }

            # 2. Publish to Kafka
            # Using the ticker as the 'key' ensures strict ordering within Kafka partitions
            producer.produce(
                topic='raw-stock-data',
                key=ticker,
                value=json.dumps(payload),
                callback=delivery_report
            )

            # Trigger the callback queue
            producer.poll(0)
            success = True
            logging.info(f"Successfully published payload for {ticker}")

        except Exception as e:
            attempts += 1
            # Exponential Backoff logic: wait 2s, 4s, 8s, etc.
            wait_time = min(base_delay * (2 ** (attempts - 1)), max_delay)
            logging.error(f"Network error fetching {ticker}: {e}. Retrying in {wait_time}s...")
            time.sleep(wait_time)

def fetch_and_publish():
    # Load dynamic ticker list instead of hardcoded array
    tickers = load_tickers("test_tickers.txt")

    if not tickers:
        logging.error("Exiting: No tickers to process.")
        exit(1)

    logging.info(f"Starting the Harvester Daemon for {len(tickers)} companies...")

    while True:
        start_time = time.time()

        # Execute concurrent fetching to handle the entire Nifty 500 quickly
        with concurrent.futures.ThreadPoolExecutor(max_workers=15) as executor:
            executor.map(process_single_ticker, tickers)

        # Flush ensures any remaining messages in internal buffers are sent
        producer.flush()

        end_time = time.time()
        logging.info(f"Market sweep complete in {round(end_time - start_time, 2)} seconds. Waiting before next ingestion cycle...\n")

        # Wait 15 minutes (900 seconds) before fetching the next round of live prices to avoid API bans
        time.sleep(900)

if __name__ == "__main__":
    fetch_and_publish()