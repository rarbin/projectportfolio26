/* global use, db */
// MongoDB Playground
// Use Ctrl+Space inside a snippet or a string literal to trigger completions.

const database = 'ORDERS';
const suppliers = 'SUPPLIERS';
const products = 'PRODUCTS';
const customers = 'CUSTOMERS';
const orders = 'ORDERS';

use("orders");

db.createCollection("suppliers");
db.createCollection("products");
db.createCollection("customers")
db.createCollection("orders")


//EX1 Supplier
db.suppliers.insertOne(
  {
    supplier_name: "Supplier A",
    location: "Location A",
    contact: "Contact A"
  }
)

db.suppliers.find().pretty() 
/*
in proj could have it find suppliers ObjectID and use that
input value automatically instead of manual objectid insertion
*/

//EX2 Products
db.products.insertOne(
  {
    description: "Product A",
    price: 99.99,
    supplier_id: ObjectId("6aeb6093b7f797225ce12e5d"),
    prod_type: ["TYPEA", "TYPEB", "TYPEC"]
  }
)

db.products.find().pretty() 


//EX 3 Customer
db.customers.insertOne(
  {
    first_name: "John",
    last_name: "Doe",
    phone: "085050505",
    email: "customera@mail.com",
    shipping_address: [
      {
        line_1: "Example",
        line_2: "Example",
        county: "Example", 
        eircode: "Example"
      }
    ],
    payment_details: {
      card_type: "Example",
      card_name: "Example",
      card_number: "0000 0000 0000 0000",
      card_expiry: "01/30"
    }
  }
)

db.customers.find().pretty() 
//EX 4 Order
db.orders.insertOne(
  {
    order_date: new Date("2026-01-01"),
    cust_id: ObjectId("6aeb60e0b7f797225ce12e5f"),
    order_line: [
      {
        product_id: ObjectId("6aeb60beb7f797225ce12e5e"),
        quantity: 9
      }
    ]
  }
)

db.orders.find().pretty() 
/*
[
  {
    _id: ObjectId('6aeb60f7b7f797225ce12e60'),
    order_date: ISODate('2026-01-01T00:00:00.000Z'),
    cust_id: ObjectId('6aeb60e0b7f797225ce12e5f'),
    order_line: [ { product_id: ObjectId('6aeb60beb7f797225ce12e5e'), quantity: 9 } ]
  }
]
*/