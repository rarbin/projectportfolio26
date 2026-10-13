use ("rescue") 
db.animals.deleteOne({_id : "2002"})
db.animals.insertOne({
    _id : "2002", 
    name: "Thorn",
    gender: "Male",
    species: "Reptile",
    breed: "Garden Snake",
    dateOfRescue: ISODate(),
    adopted: false,
    weightKg: 2.5,
    colours: ["grey", "black"],
    shelterLocation: {
      building: "St. Francis",
      houseNumber: 3
    },
     vaccinations: [
      {type:"parvovirus", frequency: "yearly", amountMg:3},
      {type:"distemper", frequency: "for life", amountMg: 5}
    ],
    intakeOfficer: "Jane O'Connor"
  });

  