db.animals.deleteOne(
    {name:"Lovely", species:"Budgie"}
)

db.animals.deleteMany(
    {species:"Hamster"}
)
//https://www.mongodb.com/docs/manual/reference/operator/query/type/
db.animals.find({
   'vaccinations.amountMg': {$lte:6}},{"vaccinations.type":"parvovirus"});


//https://www.geeksforgeeks.org/mongodb/mongodb-db-collection-deleteone/
db.animals.deleteOne({})