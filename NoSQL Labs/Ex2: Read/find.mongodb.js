use ("rescue")
/*aggregate as per; https://stackoverflow.com/questions/6502541/mongodb-query-multiple-collections-at-once
below this is the individual exercises, if you want to test them seperately*/
db.animals.aggregate([
  {
    $facet: {
      //EX1: Find ONLY male canines
      EX1: [
        { $match: { species: "Canine", gender: "Male" } }
      ],

      //EX2: Find canines OR male animals
      EX2: [
        {
          $match: {
            $or: [
              { species: "Canine" },
              { gender: "Male" }
            ]
          }
        }
      ],

      //EX3: Find poodles WITH the given tags
      EX3: [
        {
          $match: {
            breed: "Poodle",
            tags: { $in: ["friendly", "good with kids"] }
          }
        }
      ],

      //EX4: Find canines WITHOUT the given tags
      EX4: [
        {
          $match: {
            species: "Canine",
            tags: { $nin: ["timid", "quiet"] }
          }
        }
      ],

      //EX5: Find cats where weight is GREATER THAN 20kg
      EX5: [
        {
          $match: {
            species: "Cat",
            weightKg: { $gt: 20 }
          }
        }
      ],

      //EX6: Find house numbers GREATER THAN 21 and LESS THAN 30
      EX6: [
        {
          $match: {
            "shelterLocation.houseNumber": { $gt: 21, $lt: 30 }
          }
        }
      ],

      //EX7: Number of documents that meet EX6's criteria
      EX7: [
        {
          $match: {
            "shelterLocation.houseNumber": { $gt: 21, $lt: 30 }
          }
        },
        { $count: "total" }
      ],

      //EX8: Vaccination amountMg GREATER THAN 2 and LESS THAN 5
      EX8: [
        {
          $match: {
            vaccinations: {
              $elemMatch: {
                amountMg: { $gt: 2, $lt: 5 }
              }
            }
          }
        }
      ],

      //EX9: Budgies vaccinated for distemper, amountMg = 5; ONLY id, breed, name
      EX9: [
        {
          $match: {
            species: "Budgie",
            vaccinations: {
              $elemMatch: {
                vaccine: "Distemper",
                amountMg: 5
              }
            }
          }
        },
        {
          $project: {
            _id: 1,
            breed: 1,
            name: 1
          }
        }
      ],

      //EX10: Animals WERE taken by Jane O'Connor
      EX10: [
        {
          $match: {
            intakeOfficer: "Jane O'Connor"
          }
        }
      ],

      //EX10 (count): Total taken by Jane O'Connor
      EX10_COUNT: [
        {
          $match: {
            intakeOfficer: "Jane O'Connor"
          }
        },
        { $count: "total" }
      ],

      //EX11: Animals NOT taken by Jane O'Connor, name + intakeOfficer, sorted, first 5
      EX11: [
        {
          $match: {
            intakeOfficer: { $ne: "Jane O'Connor" }
          }
        },
        {
          $project: {
            _id: 0,
            name: 1,
            intakeOfficer: 1
          }
        },
        { $sort: { intakeOfficer: 1 } },
        { $limit: 5 }
      ],

      //EX12: Species catered for by the database
      EX12: [
        {
          $group: {
            _id: "$species"
          }
        },
        { $sort: { _id: 1 } }
      ]
    }
  }
]).pretty()

//to run individually, uncomment
/*
//EX1: Find ONLY male canines
db.animals.find({
  species: "Canine",
  gender: "Male"
}),
//EX2: Find canines OR male animals.
db.animals.find({
  $or: [
    { species: "Canine" },
    { gender: "Male" }
  ]
}),
//EX3: Find poodles WITH the given tags
db.animals.find({
  breed: "Poodle",
  tags: { $in: ["friendly", "good with kids"] }
}),
//EX4: Find canines WITHOUT the given tags
db.animals.find({
  species: "Canine",
  tags: { $nin: ["timid", "quiet"] }
}),
//EX5: Find cats where weight is GREATER THAN 20kg
db.animals.find({
  species: "Cat",
  weightKg: { $gt: 20 }
}),
//EX6: Find house numbers GREATER THAN 21 or LESS THAN 30
db.animals.find({
   //dot operand specifies what attribute of shelterLocation to use
  "shelterLocation.houseNumber": { $gt: 21, $lt: 30 }
}),
//EX7: Number of documents that meet criteria above
db.animals.countDocuments({
  "shelterLocation.houseNumber": { $gt: 21, $lt: 30 }
}),
//EX8: Vaccination amountMg GREATER THAN 2 and LESS THAN 5
db.animals.find({
  vaccinations: {
    $elemMatch: {
      amountMg: { $gt: 2, $lt: 5 }
    }
  }
}),
//EX9: Budgies vaccinated for distemper, amountMg = 5; ONLY animal id, breed, name
db.animals.find({
    species: "Budgie",
    vaccinations: {
      $elemMatch: {
        vaccine: "Distemper",
        amountMg: 5
      }
    }
  },
  {
    _id: 1,
    breed: 1,
    name: 1
  }),
//EX10 Animals WERE taken by client, total number
db.animals.find({
  intakeOfficer: "Jane O'Connor"
})
db.animals.countDocuments({
  intakeOfficer: "Jane O'Connor"
})
//Ex11: Animals NOT taken by client, show ONLY animal name/intake office name.
//SORT BY intakeOfficer, limited to FIRST 5
db.animals.find(
  {
    intakeOfficer: { $ne: "Jane O'Connor" }
  },
  {
    _id: 0,
    name: 1,
    intakeOfficer: 1
  }
).sort({ intakeOfficer: 1 }).limit(5)
//Ex12: Species catered for by database.
db.animals.distinct("species")
*/