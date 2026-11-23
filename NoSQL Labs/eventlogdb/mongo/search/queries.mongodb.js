use("eventlogdb")

/*actors*/
db.actors.find({ 'permission.type': 'admin' });
db.actors.find({ tags: 'Cork' });

/*AGGREGATE*/

//find all actors, user or admin, that have more than 2 tags.
db.actors.aggregate([
    {
        $match: {
            $expr: {
                $gt: [{ $size: "$tags" }, 2]
            }
        }
    },
    {
        //project selects only name/tags as opposed to full object
        //https://www.geeksforgeeks.org/mongodb/mongodb-projection/ 
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
    {
        $sort:
            { count: -1 }
    }
])

/* resources */

db.resources.find({ type: 'Windows 11' });
db.resources.find({ active: true });

/*AGGREGATE*/

//tally resources per type
db.resources.aggregate([
    {
        $group:
        {
            _id: "$type",
            count: { $sum: 1 }
        }
    },
    {
        $sort:
            { count: -1 }
    }
])

//display resources with metadata, project  only type and metadata details.
db.resources.aggregate([
    {
        $match:
            { metadata: { $exists: true } }
    },
    {
        $unwind:
            "$metadata"
    },
    {
        $project:
            { type: 1, metadata: 1 }
    }
])