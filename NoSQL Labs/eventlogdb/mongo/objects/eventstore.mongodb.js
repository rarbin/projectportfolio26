use("eventlogdb")

/*
event store for a server hosting service using actor and resource datasets.
seen here in a bank account example
https://oneuptime.com/blog/post/2026-03-31-mongodb-how-to-implement-event-sourcing-with-mongodb/view 

*/

//https://www.geeksforgeeks.org/mongodb/mongodb-db-collection-createindex-method/ 
db.eventstore.createIndex({ aggregateId: 1 })


/*
one event store object has:
unique id, an aggregate id incl. resources/actors, time of creation, and a file/vm upload if applicable.
*/
//https://stackoverflow.com/a/30492109 
db.eventstore.insertMany([
    {
        "_id": "server-1:A",
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
        "_id": "server-1:B",
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