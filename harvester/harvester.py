import time
import json
import random
from confluent_kafka import Producer

# Kafka Producer Configuration
producer = Producer({'bootstrap.servers': 'localhost:9092'})

def generate_mock_data(symbol, last_price):
    # Simulate a small percentage change (random walk)
    change_percent = random.uniform(-0.02, 0.02)
    new_price = last_price * (1 + change_percent)
    return {
        "ticker": symbol,
        "currentPrice": round(new_price, 2),
        "trailingPE": round(random.uniform(15, 30), 2),
        "returnOnEquity": round(random.uniform(0.1, 0.3), 2),
        "debtToEquity": round(random.uniform(0.5, 1.5), 2),
        "timestamp": time.time()
    }

# Track prices in memory to simulate movement
stocks = {"RELIANCE.NS": 2500.0, "TCS.NS": 3200.0, "INFY.NS": 1500.0}

print("Starting synthetic data stream to Kafka...")
while True:
    for symbol, price in stocks.items():
        data = generate_mock_data(symbol, price)
        stocks[symbol] = data["currentPrice"] # Update last price

        # Send to Kafka
        producer.produce('raw-stock-data', key=symbol, value=json.dumps(data))

    producer.flush()
    time.sleep(1) # Send a new batch every second