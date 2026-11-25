use("eventlogdb")


//TBD server status/cloud storage searches that display server status/free space 
//have some premade files, attach it directly to resources
//webhook integration? maybe send the resource/actor audit

/*actors*/
db.actors.find({ 'permission.type': 'admin' });
db.actors.find({ tags: 'Cork' });

/*AGGREGATE*/

//find all actors, user or admin, that have more than 2 tags.
db.actors.aggregate([
    {
        $match: {
            $expr: { $gt: [{ $size: "$tags" }, 2] }
        }
    },
    {
        //project selects only name/tags as opposed to full object //https://www.geeksforgeeks.org/mongodb/mongodb-projection/ 
        //https://www.mongodb.com/docs/v7.0/reference/operator/aggregation/project/ 
        $project:
            { name: 1, tags: 1 }
    }
])

//sort actors by permission type, then tallies their total
//https://stackoverflow.com/a/22534678 

db.actors.aggregate([
    { $unwind: "$permission" },
    {
        $group: {
            _id: "$permission.type",
            count: { $sum: 1 }
        }
    },
    { $sort: { count: -1 } }
])

/* resources */
//find resources of type 'win 11', vm is presumed
db.resources.find({ type: 'Windows 11' });
//find active resources
db.resources.find({ active: true });

//tally resources per type
db.resources.aggregate([
    {    $group:
        {
            _id: "$type",
            count: { $sum: 1 }
        }
    },
    { $sort: { count: -1 } }
])

//display resources with metadata, project  only type and metadata details.
db.resources.aggregate([
    { $match: { metadata: { $exists: true } } },
    { $unwind: "$metadata" }, //unwinding makes nested attributes accessible
    { $project: { type: 1, metadata: 1 } }
])

/*EVENT STORE*/

//all actions/events on server-1 sorted by time (ascending/1)
db.eventstore.find({ aggregateId: "server-1" }).sort({ _id: 1 })

//all actions/events taken by given actor 

//-1 allows searching backwards in the index, which is potentially faster depending on find location. https://stackoverflow.com/a/74778161
db.eventstore.find({ "actor.id": "Admin-A" }).sort({ timeStamp: -1 })

//action/event history involved with Cloud-A
db.eventstore.find({ "resource.id": "Cloud-A" }).sort({ timeStamp: -1 })


//all events in the last hour
//https://stackoverflow.com/a/13318076 

db.eventstore.find({ timeStamp: { $gte: new Date(Date.now() - 3600000) } })
    .sort({ timeStamp: -1 })
    .limit(100);

//by 24 hours and organised by type

db.eventstore.aggregate([
    { $match: { timeStamp: { $gte: new Date(Date.now() - 86400000) } } },
    {
        $group: {
            _id: '$eventType',
            count: { $sum: 1 },
            recentTS: { $max: '$timeStamp' }
        }
    },
    { $sort: { count: -1 } },
    { $limit: 20 }
]);


//activity in a given date range
//https://stackoverflow.com/a/2943685 
db.eventstore.find({
    'actor.id': 'Admin-A',
    timeStamp: {
        $gte: ISODate('2026-11-20T00:00:00Z'),
        $lt: ISODate('2026-11-24T00:00:00Z')
    }
}).sort({ timeStamp: -1 });

//find all event type 'file added' in the last hour
db.eventstore.find({
    eventType: { $in: ['FileAdded'] },
    timeStamp: { $gte: new Date(Date.now() - 3600000) }
}).sort({ timeStamp: -1 });


//event counts by type

db.eventstore.aggregate([
    { $group: { _id: "$eventType", count: { $sum: 1 }, last: { $max: "$timeStamp" } } },
    { $sort: { count: -1 } }
])

//specific actor activity, to array 'actorAudit'
//https://www.geeksforgeeks.org/mongodb/mongodb-aggregation-lookup/ 
db.eventstore.aggregate([
    { $match: { 'actor.id': 'Admin-B' } },
    { $sort: { timeStamp: -1 } },
    { $lookup: { from: 'actors', localField: 'actor.id', foreignField: '_id', as: 'actorAudit' } },
    { $project: { eventType: 1, timeStamp: 1, 'actorAudit.name': 1, aggregateId: 1 } }
]);

//specific resource activity, to array 'resourceAudit'
db.eventstore.aggregate([
    { $match: { aggregateId: 'server-1' } },
    { $group: { _id: '$resource.id', events: { $sum: 1 } } },
    { $lookup: { from: 'resources', localField: '_id', foreignField: '_id', as: 'resourceAudit' } }
]);

//combined resource/actor audit
db.eventstore.aggregate([
    { $match: { aggregateId: 'server-1' } },
    { $sort: { timeStamp: -1 } },
    { $lookup: { from: 'actors', localField: 'actor.id', foreignField: '_id', as: 'actorAudit' } },
    { $lookup: { from: 'resources', localField: 'resource.id', foreignField: '_id', as: 'resourceAudit' } },
    {
        $project: {
            eventType: 1,
            timeStamp: 1,
            upload: 1,
            'actorDoc.name': 1,
            'actorDoc.permission': 1,
            'resourceDoc.type': 1,
            'resourceDoc.RAM': 1
        }
    }
]); 