use("eventlogdb")

/*
event store for a server hosting service using actor and resource datasets.
seen here in a bank account example
https://oneuptime.com/blog/post/2026-03-31-mongodb-how-to-implement-event-sourcing-with-mongodb/view 

*/


//https://oneuptime.com/blog/post/2026-03-31-mongodb-implement-event-replay/view
//https://www.geeksforgeeks.org/mongodb/mongodb-db-collection-createindex-method/ 
db.eventstore.createIndex({ aggregateId: 1 })
db.eventstore.createIndex({aggregateType: 1, occuredAt: 1}) 
db.eventstore.createIndex({eventType: 1,  occuredAt: -1})
db.eventstore.createIndex({"actor.id": 1, occuredAt: -1})
db.eventstore.createIndex({"resource.id": 1, occuredAt: -1})

/*
one event store object has:
unique id, an aggregate id incl. resources/actors, time of creation, and a file/vm upload if applicable.
*/
//https://stackoverflow.com/a/30492109 
db.eventstore.insertMany([
    {
        _id: "server-1:1",
        "aggregateId": "server-1", "aggregateType": "WinCloud",
        "eventType": "ServerStarted",
        "timeStamp": ISODate(),
        "upload": {},
        resource: {
            "id": "VM-A",
            "type": "Windows 11"
        },
        actor: {
            "id": "Admin-A",
            "ip": "192.168.1.3"
        }
    },
    {
        _id: "server-1:2",
        "aggregateId": "server-1", "aggregateType": "WinCloud",
        "eventType": "ServerStopped",
        "timeStamp": ISODate(),
        "upload": {},
        resource: {
            "id": "VM-A",
            "type": "Windows 11"
        },
        actor: {
            "id": "Admin-A",
            "ip": "192.168.1.3"
        }
    }
])

/*UPDATE*/

/* originally, i was trying to get ids to update incrementally here, but converting the lastId cursor to json (to be able to add to the nextId) and not have it come out as [object Object] is above my paygrade haha. 


https://joshtronic.com/2020/07/19/how-to-get-the-first-and-last-item-from-an-array-in-mongodb/ */

/*
doesnt work 
db.eventstore.find({}, { _id: 1, array: { $slice: -1 } });

db.eventstore.findOne({ aggregateId: "server-1" }).sort({ _id: -1 }).projection({_id:1}).limit(1) 

//expr is supposed to stop it being a cursor, /supposed to/.
db.eventstore.find({ $expr: {$eq:[{$last: "$eventstore" }, "1"] }})


const lastId = db.eventstore.find({$expr:{ aggregateId: "server-1" }).sort({ _id: -1 }).projection({_id:1}).limit(1) 
*/

//append a new file eventc, after checking for the last number in the sequence

//get last id
db.eventstore.find({ aggregateId: "server-1" }).sort({ _id: -1 }).projection({_id:1}).limit(1) 

//insert new
db.eventstore.insertOne({
  _id: "server-1:3",
  aggregateId: "server-1", aggregateType: "UbuntuCloud",
  eventType: "FileAdded",
  timeStamp: ISODate(),
  upload: {fileId: "cool.png"},
  resource: { id: "Cloud-A", type: "cloudStorage" },
  actor:    { id: "Admin-B", type: "admin", ip: "192.168.1.4" }
}) 


//find last event for its sequence number
db.eventstore.find({aggregateId:"server-1"}).sort({_id:-1}).limit(1) 


//append file remove IF the file has been added
db.eventstore.find({ aggregateId: "server-1", eventType: "FileAdded" })

db.eventstore.insertOne({
  _id: "server-1:4",
  aggregateId: "server-1", aggregateType: "WinCloud",
  eventType: "FileRemoved",
  occurredAt: ISODate(),
  payload: { fileId: "file-abc" },
  resource: { id: "Cloud-A", type: "cloudStorage" },
  actor:    { id: "Admin-B", type: "admin", ip: "192.168.1.4" }
})

/*DELETE*/
//delete all
/*
db.eventstore.deleteMany({ aggregateId: "server-1" })
*/

//rebuilding events from log TBD