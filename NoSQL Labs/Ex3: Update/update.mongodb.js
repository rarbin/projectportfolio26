// Exercises - Use a find() to verify the changes
//while not demonstrated here, aggregate could be used to run all at once 
//EX 1. update animal named catty with breed british shorthair, set adopted to true

//find
db.animals.find({name:"Catty",breed:"British Shorthair"})
//update
db.animals.updateOne({
    name:"Catty",
    breed:"British Shorthair"
},
{ $set: {
    adopted: true 
}}
)

//EX 2. update cat lucy with breed syrian, vac
//third array, type distemper, frequency yearly and amountmg 2
//order of set matters for display in DB, e.g. these print in reverse order
db.animals.find({name:"Lucy",breed:"Syrian"})
 
db.animals.updateOne({
    name:"Lucy",
    breed: "Syrian"
},
{$set:
{"vaccinations.3.type": 'distemper',
    "vaccinations.3.frequency": 'yearly',
    "vaccinations.3.amountMg": 2
}
})

//EX 3.  upsert (add if not exist) true, add id MC-3002
// gender female, species reptile, breed unknown, rescued today, adopted false, colours red/white/black, tags poisonous, tame, intakeofficer jane o connor, vaccinations calcivirus, monthly,5mg

db.animals.find({_id:"MC-3002"})

db.animals.updateOne({
    _id:"MC-3002"
},
{$set:
  {  gender:"female",
     species:"reptile",
     breed:"unknown",
     rescued:"today",
     adopted:"false",
     colours:"red/white/black",

     tags:[
      "poisonous", "tame"
  ],
    intakeOfficer:"Jane O'Connor",
    "vaccinations.type": 'calcivirus',
    "vaccinations.frequency": 'monthly',
    "vaccinations.amountMg": 5

    }
},
{upsert:true})


//EX 4. push  (add onto) tag  'very very cute' to mc 1023
db.animals.updateOne({
    _id:"MC-1023"
},
//push is a seperate command from set
 {$push: { tags: "very very cute :)" } 
 }
)

//EX 5. remove last tag (1) where first tag would be (-1)
db.animals.updateOne(
{ _id : "MC-1013" },
 { $pop : { tags: 1 } }
)


//EX 6. updateMany, push
//push chubby to all, no criteria
db.animals.updateMany(
{ },
{ $push: { tags: "chubby" } }
)

//EX 7 updateMany, pull
db.animals.updateMany(
{ },
{ $pull: { tags: "chubby" } }
)


//EX 8  all animal vac amountMg >=2 and double, array filter
//this just finds all >=2
db.animals.find({
   'vaccinations.amountMg': {$gte: 2}
 }
  );

//aggregates finding with multiplying, mapping because its an array and otherwise
//it fails the operation
//https://www.mongodb.com/docs/v7.0/tutorial/query-array-of-documents/?interface=driver&language=nodejs#std-label-array-match-embedded-documents 
db.animals.aggregate([ {
 $match: {
    'vaccinations.amountMg': {$gte: 2}
}
  },  {
    $addFields:{
        convertedVac: {
            $map: {
                input:"$vaccinations.amountMg",
                as: "vA", //turning it into a multipliable version
                in: { $multiply:["$$vA", 2]}
            }
        }
    }}
])