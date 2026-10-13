use("mydatabase")
db.dropDatabase("mydatabase")
use("mydatabase")

db.mydatabase.insertOne({ 
    item: "box", 
    qty: 20, 
    price: 14.99, 
    available: true, 
    timestamp: new Date(), 
    itemcodes: [1002, 1003, 1004], 
    supplierinfo:{ name: "Boxes Inc.", location: "Ireland"} 
});

db.mydatabase.find().pretty()
