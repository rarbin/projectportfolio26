use ("rescue")
db.animals.deleteOne({_id : "MC-2004"})
db.animals.deleteOne({_id : "MC-2005"})
db.animals.deleteOne({_id : "MC-2007"})
db.animals.insertMany([
{
  "_id": "MC-2004",
  "name": "Pup1",
  "gender": "Male",
  "species": "Canine",
  "breed": "Mongrel",
  "dateOfBirth": ISODate(),
  "dateOfRescue": ISODate(),
  "adopted": false,
  "weightKg": 1,
  "colours": [
    "black",
    "white"
  ],
  "shelterLocation": {
    "building": "St. Bridget",
    "houseNumber": 33
  },
  "neutered": false,
  "tags": [
    "underweight",
    "needs vaccination",
  ],
  "intakeOfficer": "Liam Walsh"
},
{  "_id": "MC-2005",
  "name": "Pup2",
  "gender": "Male",
  "species": "Canine",
  "breed": "Mongrel",
  "dateOfBirth": ISODate(),
  "dateOfRescue": ISODate(),
  "adopted": false,
  "weightKg": 1,
  "colours": [
    "dapple"
  ],
  "shelterLocation": {
    "building": "St. Bridget",
    "houseNumber": 33
  },
  "neutered": false,
  "tags": [
    "underweight",
    "needs vaccination",
  ],
  "intakeOfficer": "Liam Walsh"
},
{  "_id": "MC-2007",
  "name": "Pup3",
  "gender": "Female",
  "species": "Canine",
  "breed": "Mongrel",
  "dateOfBirth": ISODate(),
  "dateOfRescue": ISODate(),
  "adopted": false,
  "weightKg": 0.8,
  "colours": [
    "brown"
  ],
  "shelterLocation": {
    "building": "St. Bridget",
    "houseNumber": 33
  },
  "neutered": false,
  "tags": [
    "underweight",
    "needs vaccination",
  ],
  "intakeOfficer": "Liam Walsh"}
])