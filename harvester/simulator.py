import time
import json
import random
from confluent_kafka import Producer

# Kafka Producer Configuration
conf = {'bootstrap.servers': 'localhost:9092'}
producer = Producer(conf)

# Generate a list of 500 Nifty stock tickers
tickers = [f"STOCK_{i}.NS" for i in range(1, 501)]

def produce_mock_data():
    print("Starting high-throughput simulation...")
    while True:
        for ticker in tickers:
            # Simulate a market tick
            data = {
                "ticker": ticker,
                "currentPrice": round(random.uniform(100, 5000), 2),
                "trailingPE": round(random.uniform(10, 50), 2),
                "returnOnEquity": round(random.uniform(0.05, 0.30), 4),
                "debtToEquity": round(random.uniform(0.1, 2.0), 2)
            }
            print(f"Attempting to produce: {ticker}")
            # Asynchronous delivery
            producer.produce('raw-stock-data', key=ticker, value=json.dumps(data))

        print("Flushing to Kafka...")
        producer.flush()
        print("Batch complete. Sleeping...")
        time.sleep(0.5) # Simulates high-frequency market updates

if __name__ == "__main__":
    produce_mock_data()