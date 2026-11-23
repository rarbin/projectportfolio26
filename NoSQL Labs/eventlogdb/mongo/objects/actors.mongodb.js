use("eventlogdb")

/*CREATE*/
/*
add both standard and administrator users to the database.
insertOne would also work here
*/
db.actors.insertMany([
    {
        "_id": "User-A",
        "name": "John",
        "permission": [
            {
                "type": "user",
                "privileged": false
            }
        ],
        "ip": "192.168.1.1",
        "userAgent": "Mozilla",
        "tags": ["Waterford", "Part-Time"]
    },
    {
        "_id": "User-B",
        "name": "Jane",
        "permission": [
            {
                "type": "user",
                "privileged": false
            }
        ],
        "ip": "192.168.1.2",
        "userAgent": "Internet Explorer",
        "tags": ["Cork", "Full-Time"]
    },
    {
        "_id": "Admin-A",
        "name": "Mary",
        "permission": [
            {
                "type": "admin",
                "privileged": true
            }
        ],
        "ip": "192.168.1.3",
        "userAgent": "Mozilla",
        "tags": ["Kerry", "Full-Time"]
    },
    {
        "_id": "Admin-B",
        "name": "Mark",
        "permission": [
            {
                "type": "admin",
                "privileged": true
            }
        ],
        "ip": "192.168.1.4",
        "userAgent": "Microsoft Edge",
        "tags": ["Galway", "Part-Time"]
    }
])

/*READ*/
//will keep all find to queries.js

/*UPDATE*/
db.actors.updateOne({
    _id: "User-A",
    name: "John"
},
    {
        $set: {
            "permission.0.type":
                "admin",
            "permission.0.privileged":
                true
        }
    }
)

/*to add onto existing data
  using push
*/
db.actors.updateOne({
    _id: "User-A",
    name: "John"
},
    {
        $push: { tags: "Winter Contract" }
    }
)
db.actors.updateMany(
    {},
    { $push: { tags: "Winter Contract" } }
)

/* remove first tag (1) or last tag (-1)*/
db.actors.updateOne({
    _id: "User-A",
    name: "John"
},
    {
        $pop: { tags: 1 }
    }
)

/*DELETE*/
/*
db.actors.deleteOne(
     { _id:"User-A",
    name:"John"}
)
    */
/*
 db.actors.deleteMany(
    {userAgent:"Internet Explorer"}
)
*/