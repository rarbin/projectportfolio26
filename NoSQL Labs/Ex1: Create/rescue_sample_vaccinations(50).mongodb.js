use("rescue")
db.dropDatabase();
db.animals.insertMany(
[
  {
    "_id": "MC-1002",
    "name": "Ollie",
    "gender": "Male",
    "species": "Canine",
    "breed": "Golden Retriever",
    "dateOfBirth": "2020-06-15T00:00:00Z",
    "dateOfRescue": "2024-11-05T00:00:00Z",
    "adopted": false,
    "weightKg": 24.5,
    "colours": [
      "golden",
      "white"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 12
    },
    "vetVisit": {
      "lastVetVisit": "2025-03-15T00:00:00Z",
      "notes": "Recovered well from skin infection. Needs follow-up in 6 months."
    },
    "tags": [
      "friendly",
      "good with kids",
      "active"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1003",
    "name": "Chloe",
    "gender": "Female",
    "species": "Cat",
    "breed": "Persian",
    "dateOfBirth": "2016-04-16T00:00:00Z",
    "dateOfRescue": "2021-08-29T00:00:00Z",
    "adopted": false,
    "weightKg": 16.2,
    "colours": [
      "white"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 49
    },
    "vetVisit": {
      "lastVetVisit": "2022-06-21T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "quiet",
      "good with kids",
      "friendly"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1004",
    "name": "Sheila",
    "gender": "Female",
    "species": "Canine",
    "breed": "Bulldog",
    "dateOfBirth": "2019-02-16T00:00:00Z",
    "dateOfRescue": "2020-11-04T00:00:00Z",
    "adopted": true,
    "weightKg": 1.2,
    "colours": [
      "white",
      "tan",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 24
    },
    "vetVisit": {
      "lastVetVisit": "2020-12-06T00:00:00Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "timid",
      "quiet",
      "needs training"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1005",
    "name": "Grant",
    "gender": "Male",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2022-02-15T00:00:00Z",
    "dateOfRescue": "2024-08-12T00:00:00Z",
    "adopted": false,
    "weightKg": 1.2,
    "colours": [
      "tan"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 21
    },
    "vetVisit": {
      "lastVetVisit": "2024-12-10T00:00:00Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "friendly",
      "good with kids",
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1006",
    "name": "Sarah",
    "gender": "Female",
    "species": "Cat",
    "breed": "British Shorthair",
    "dateOfBirth": "2021-01-15T00:00:00Z",
    "dateOfRescue": "2025-04-23T08:20:39.154884Z",
    "adopted": true,
    "weightKg": 5.0,
    "colours": [
      "golden",
      "black",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 33
    },
    "vetVisit": {
      "lastVetVisit": "2025-07-23T08:20:39.154884Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "good with kids",
      "quiet",
      "needs training"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1007",
    "name": "Harry",
    "gender": "Male",
    "species": "Cat",
    "breed": "Siamese",
    "dateOfBirth": "2016-08-25T00:00:00Z",
    "dateOfRescue": "2019-08-20T00:00:00Z",
    "adopted": false,
    "weightKg": 6.2,
    "colours": [
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 46
    },
    "vetVisit": {
      "lastVetVisit": "2020-04-26T00:00:00Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "timid",
      "good with kids"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1008",
    "name": "Lilli",
    "gender": "Female",
    "species": "Canine",
    "breed": "Red Setter",
    "dateOfBirth": "2019-08-09T00:00:00Z",
    "dateOfRescue": "2025-03-23T08:20:39.155030Z",
    "adopted": false,
    "weightKg": 26.8,
    "colours": [
      "orange",
      "red"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 35
    },
    "vetVisit": {
      "lastVetVisit": "2025-08-16T08:20:39.155030Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "active",
      "needs training"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1009",
    "name": "George",
    "gender": "Male",
    "species": "Cat",
    "breed": "unknown",
    "dateOfBirth": "2023-02-03T00:00:00Z",
    "dateOfRescue": "2025-05-16T08:20:39.155093Z",
    "adopted": false,
    "weightKg": 3.9,
    "colours": [
      "cream",
      "black"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 30
    },
    "vetVisit": {
      "lastVetVisit": "2025-12-28T08:20:39.155093Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "active",
      "needs training",
      "good with kids",
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      }
    ]
  },
  {
    "_id": "MC-1010",
    "name": "Anne",
    "gender": "Female",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2021-04-13T00:00:00Z",
    "dateOfRescue": "2025-04-09T08:20:39.155162Z",
    "adopted": true,
    "weightKg": 3.2,
    "colours": [
      "tan",
      "black",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 32
    },
    "vetVisit": {
      "lastVetVisit": "2025-11-01T08:20:39.155162Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "good with kids",
      "needs training",
      "timid"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1011",
    "name": "Simba",
    "gender": "Male",
    "species": "Cat",
    "breed": "British Shorthair",
    "dateOfBirth": "2020-01-24T00:00:00Z",
    "dateOfRescue": "2025-05-28T08:20:39.155233Z",
    "adopted": true,
    "weightKg": 39.3,
    "colours": [
      "white"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 36
    },
    "vetVisit": {
      "lastVetVisit": "2026-01-31T08:20:39.155233Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "needs training",
      "timid",
      "active"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1012",
    "name": "Catty",
    "gender": "Male",
    "species": "Cat",
    "breed": "British Shorthair",
    "dateOfBirth": "2020-12-10T00:00:00Z",
    "dateOfRescue": "2024-09-18T00:00:00Z",
    "adopted": false,
    "weightKg": 27.0,
    "colours": [
      "black",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 8
    },
    "vetVisit": {
      "lastVetVisit": "2025-02-09T00:00:00Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "good with kids"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1013",
    "name": "Poppy",
    "gender": "Male",
    "species": "Hamster",
    "breed": "Syrian",
    "dateOfBirth": "2023-11-15T00:00:00Z",
    "dateOfRescue": "2024-10-17T00:00:00Z",
    "adopted": false,
    "weightKg": 19.8,
    "colours": [
      "brown",
      "black",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 13
    },
    "vetVisit": {
      "lastVetVisit": "2025-04-03T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "quiet",
      "friendly",
      "good with kids",
      "needs training"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1014",
    "name": "Pudgie",
    "gender": "Female",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2021-12-12T00:00:00Z",
    "dateOfRescue": "2025-01-31T00:00:00Z",
    "adopted": true,
    "weightKg": 34.4,
    "colours": [
      "black",
      "tan"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 6
    },
    "vetVisit": {
      "lastVetVisit": "2025-05-04T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "needs training",
      "timid",
      "quiet",
      "friendly"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1015",
    "name": "Bob",
    "gender": "Male",
    "species": "Hamster",
    "breed": "Dwarf Campbell",
    "dateOfBirth": "2015-05-24T00:00:00Z",
    "dateOfRescue": "2022-03-08T00:00:00Z",
    "adopted": false,
    "weightKg": 26.5,
    "colours": [
      "black",
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 45
    },
    "vetVisit": {
      "lastVetVisit": "2022-08-14T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "good with kids",
      "friendly",
      "active"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1016",
    "name": "Lucy",
    "gender": "Female",
    "species": "Hamster",
    "breed": "Syrian",
    "dateOfBirth": "2019-12-30T00:00:00Z",
    "dateOfRescue": "2022-01-07T00:00:00Z",
    "adopted": true,
    "weightKg": 32.2,
    "colours": [
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 7
    },
    "vetVisit": {
      "lastVetVisit": "2022-02-19T00:00:00Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "timid",
      "good with kids",
      "needs training",
      "friendly"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1017",
    "name": "Maxine",
    "gender": "Female",
    "species": "Canine",
    "breed": "Bulldog",
    "dateOfBirth": "2019-03-04T00:00:00Z",
    "dateOfRescue": "2020-02-21T00:00:00Z",
    "adopted": false,
    "weightKg": 13.9,
    "colours": [
      "brown",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 29
    },
    "vetVisit": {
      "lastVetVisit": "2020-07-16T00:00:00Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      }
    ]
  },
  {
    "_id": "MC-1018",
    "name": "Luna",
    "gender": "Female",
    "species": "Hamster",
    "breed": "Chinese",
    "dateOfBirth": "2018-08-31T00:00:00Z",
    "dateOfRescue": "2025-03-05T08:20:39.155771Z",
    "adopted": true,
    "weightKg": 8.0,
    "colours": [
      "gray",
      "golden",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 21
    },
    "vetVisit": {
      "lastVetVisit": "2025-05-23T08:20:39.155771Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "good with kids",
      "needs training",
      "quiet",
      "friendly"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1019",
    "name": "Bran",
    "gender": "Male",
    "species": "Cat",
    "breed": "Persian",
    "dateOfBirth": "2019-01-22T00:00:00Z",
    "dateOfRescue": "2025-05-30T08:20:39.155825Z",
    "adopted": true,
    "weightKg": 10.4,
    "colours": [
      "cream",
      "gray",
      "tan"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 49
    },
    "vetVisit": {
      "lastVetVisit": "2025-06-29T08:20:39.155825Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "good with kids",
      "needs training",
      "friendly",
      "active"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1020",
    "name": "Ellie",
    "gender": "Female",
    "species": "Canine",
    "breed": "Labrador",
    "dateOfBirth": "2017-01-29T00:00:00Z",
    "dateOfRescue": "2024-03-09T00:00:00Z",
    "adopted": true,
    "weightKg": 17.1,
    "colours": [
      "tan",
      "cream"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 49
    },
    "vetVisit": {
      "lastVetVisit": "2024-06-12T00:00:00Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "quiet"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      }
    ]
  },
  {
    "_id": "MC-1021",
    "name": "Jimmy",
    "gender": "Male",
    "species": "Canine",
    "breed": "Bassett Hound",
    "dateOfBirth": "2022-12-08T00:00:00Z",
    "dateOfRescue": "2025-04-29T08:20:39.155935Z",
    "adopted": false,
    "weightKg": 1.7,
    "colours": [
      "cream",
      "tan"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 42
    },
    "vetVisit": {
      "lastVetVisit": "2026-02-14T08:20:39.155935Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "good with kids",
      "active",
      "friendly",
      "quiet"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1022",
    "name": "Cutey",
    "gender": "Male",
    "species": "Hamster",
    "breed": "Dwarf Campbell",
    "dateOfBirth": "2016-08-25T00:00:00Z",
    "dateOfRescue": "2023-02-27T00:00:00Z",
    "adopted": true,
    "weightKg": 27.2,
    "colours": [
      "black",
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 48
    },
    "vetVisit": {
      "lastVetVisit": "2023-07-04T00:00:00Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      }
    ]
  },
  {
    "_id": "MC-1023",
    "name": "Rob",
    "gender": "Male",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2022-12-14T00:00:00Z",
    "dateOfRescue": "2025-05-01T08:20:39.156044Z",
    "adopted": false,
    "weightKg": 0.4,
    "colours": [
      "tan"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 16
    },
    "vetVisit": {
      "lastVetVisit": "2026-02-05T08:20:39.156044Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "timid",
      "good with kids",
      "quiet",
      "friendly"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1024",
    "name": "Hammy",
    "gender": "Female",
    "species": "Hamster",
    "breed": "Syrian",
    "dateOfBirth": "2022-10-18T00:00:00Z",
    "dateOfRescue": "2025-05-03T08:20:39.156100Z",
    "adopted": false,
    "weightKg": 30.8,
    "colours": [
      "white"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 42
    },
    "vetVisit": {
      "lastVetVisit": "2026-01-10T08:20:39.156100Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "good with kids",
      "active"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1025",
    "name": "Pete",
    "gender": "Male",
    "species": "Hamster",
    "breed": "Roborovski",
    "dateOfBirth": "2023-11-28T00:00:00Z",
    "dateOfRescue": "2025-03-30T08:20:39.156165Z",
    "adopted": false,
    "weightKg": 33.9,
    "colours": [
      "white",
      "gray",
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 4
    },
    "vetVisit": {
      "lastVetVisit": "2025-06-04T08:20:39.156165Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "friendly",
      "active",
      "good with kids",
      "quiet"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1026",
    "name": "Ben",
    "gender": "Male",
    "species": "Cat",
    "breed": "Bengal",
    "dateOfBirth": "2016-10-28T00:00:00Z",
    "dateOfRescue": "2017-12-24T00:00:00Z",
    "adopted": false,
    "weightKg": 22.4,
    "colours": [
      "white",
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 47
    },
    "vetVisit": {
      "lastVetVisit": "2018-07-25T00:00:00Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "quiet",
      "good with kids",
      "needs training"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1027",
    "name": "Loopy",
    "gender": "Male",
    "species": "Hamster",
    "breed": "Chinese",
    "dateOfBirth": "2016-12-21T00:00:00Z",
    "dateOfRescue": "2019-01-03T00:00:00Z",
    "adopted": true,
    "weightKg": 32.0,
    "colours": [
      "white",
      "gray",
      "cream"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 23
    },
    "vetVisit": {
      "lastVetVisit": "2019-09-24T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "quiet"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1028",
    "name": "Lovely",
    "gender": "Male",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2016-07-04T00:00:00Z",
    "dateOfRescue": "2021-03-05T00:00:00Z",
    "adopted": false,
    "weightKg": 26.7,
    "colours": [
      "brown",
      "black"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 5
    },
    "vetVisit": {
      "lastVetVisit": "2021-12-05T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "quiet"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1029",
    "name": "Matey",
    "gender": "Male",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2021-03-29T00:00:00Z",
    "dateOfRescue": "2023-08-24T00:00:00Z",
    "adopted": true,
    "weightKg": 7.1,
    "colours": [
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 34
    },
    "vetVisit": {
      "lastVetVisit": "2024-05-05T00:00:00Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "needs training",
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1030",
    "name": "Peppa",
    "gender": "Male",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2023-05-18T00:00:00Z",
    "dateOfRescue": "2025-03-12T08:20:39.156444Z",
    "adopted": false,
    "weightKg": 5.2,
    "colours": [
      "gray",
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 1
    },
    "vetVisit": {
      "lastVetVisit": "2025-06-04T08:20:39.156444Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "active"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1031",
    "name": "Birdy",
    "gender": "Male",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2015-03-19T00:00:00Z",
    "dateOfRescue": "2021-05-25T00:00:00Z",
    "adopted": true,
    "weightKg": 0.3,
    "colours": [
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 13
    },
    "vetVisit": {
      "lastVetVisit": "2022-02-07T00:00:00Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "timid",
      "needs training"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1032",
    "name": "Rocky",
    "gender": "Male",
    "species": "Cat",
    "breed": "Siamese",
    "dateOfBirth": "2022-07-25T00:00:00Z",
    "dateOfRescue": "2025-05-15T08:20:39.156565Z",
    "adopted": true,
    "weightKg": 37.3,
    "colours": [
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 50
    },
    "vetVisit": {
      "lastVetVisit": "2025-11-28T08:20:39.156565Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "timid",
      "needs training"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1033",
    "name": "Boopy",
    "gender": "Female",
    "species": "Cat",
    "breed": "British Shorthair",
    "dateOfBirth": "2020-06-09T00:00:00Z",
    "dateOfRescue": "2025-04-11T08:20:39.156619Z",
    "adopted": true,
    "weightKg": 24.0,
    "colours": [
      "white",
      "black",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 5
    },
    "vetVisit": {
      "lastVetVisit": "2025-07-02T08:20:39.156619Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "friendly",
      "good with kids"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      }
    ]
  },
  {
    "_id": "MC-1034",
    "name": "Chilly",
    "gender": "Male",
    "species": "Canine",
    "breed": "Beagle",
    "dateOfBirth": "2017-02-11T00:00:00Z",
    "dateOfRescue": "2023-11-13T00:00:00Z",
    "adopted": true,
    "weightKg": 1.7,
    "colours": [
      "tan",
      "golden",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 36
    },
    "vetVisit": {
      "lastVetVisit": "2024-04-12T00:00:00Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "timid",
      "quiet"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1035",
    "name": "Lucky",
    "gender": "Female",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2018-02-23T00:00:00Z",
    "dateOfRescue": "2022-03-28T00:00:00Z",
    "adopted": true,
    "weightKg": 35.8,
    "colours": [
      "white"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 34
    },
    "vetVisit": {
      "lastVetVisit": "2022-08-30T00:00:00Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "needs training",
      "friendly",
      "timid"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1036",
    "name": "Rockstar",
    "gender": "Male",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2016-09-21T00:00:00Z",
    "dateOfRescue": "2023-11-30T00:00:00Z",
    "adopted": false,
    "weightKg": 25.4,
    "colours": [
      "gray",
      "black",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 33
    },
    "vetVisit": {
      "lastVetVisit": "2024-03-22T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "quiet",
      "friendly",
      "needs training",
      "good with kids"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1037",
    "name": "Sean",
    "gender": "Male",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2016-11-29T00:00:00Z",
    "dateOfRescue": "2017-12-13T00:00:00Z",
    "adopted": false,
    "weightKg": 25.0,
    "colours": [
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 10
    },
    "vetVisit": {
      "lastVetVisit": "2018-08-31T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "active"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1038",
    "name": "Concepta",
    "gender": "Female",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2020-02-13T00:00:00Z",
    "dateOfRescue": "2020-11-27T00:00:00Z",
    "adopted": false,
    "weightKg": 25.8,
    "colours": [
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 14
    },
    "vetVisit": {
      "lastVetVisit": "2021-09-08T00:00:00Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "quiet",
      "friendly"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1039",
    "name": "Simba",
    "gender": "Female",
    "species": "Cat",
    "breed": "Bengal",
    "dateOfBirth": "2021-11-26T00:00:00Z",
    "dateOfRescue": "2025-04-29T08:20:39.156946Z",
    "adopted": false,
    "weightKg": 1.5,
    "colours": [
      "tan",
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 26
    },
    "vetVisit": {
      "lastVetVisit": "2026-01-29T08:20:39.156946Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "good with kids",
      "timid"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1040",
    "name": "Ernie",
    "gender": "Male",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2022-11-11T00:00:00Z",
    "dateOfRescue": "2025-05-18T08:20:39.157001Z",
    "adopted": true,
    "weightKg": 6.9,
    "colours": [
      "tan",
      "black"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 4
    },
    "vetVisit": {
      "lastVetVisit": "2025-12-18T08:20:39.157001Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "timid"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1041",
    "name": "Bert",
    "gender": "Male",
    "species": "Canine",
    "breed": "Great Dane",
    "dateOfBirth": "2021-11-26T00:00:00Z",
    "dateOfRescue": "2025-05-16T08:20:39.157051Z",
    "adopted": false,
    "weightKg": 117.8,
    "colours": [
      "white",
      "cream",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 31
    },
    "vetVisit": {
      "lastVetVisit": "2026-01-29T08:20:39.157051Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "good with kids",
      "quiet",
      "active",
      "friendly"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1042",
    "name": "Simba",
    "gender": "Female",
    "species": "Cat",
    "breed": "British Shorthair",
    "dateOfBirth": "2021-03-11T00:00:00Z",
    "dateOfRescue": "2022-09-24T00:00:00Z",
    "adopted": true,
    "weightKg": 29.0,
    "colours": [
      "gray",
      "tan",
      "cream"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 26
    },
    "vetVisit": {
      "lastVetVisit": "2023-05-15T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "friendly"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1043",
    "name": "George",
    "gender": "Male",
    "species": "Canine",
    "breed": "Poodle",
    "dateOfBirth": "2022-10-27T00:00:00Z",
    "dateOfRescue": "2025-05-06T08:20:39.157159Z",
    "adopted": true,
    "weightKg": 11.1,
    "colours": [
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 31
    },
    "vetVisit": {
      "lastVetVisit": "2025-08-25T08:20:39.157159Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "friendly"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1044",
    "name": "Spot",
    "gender": "Male",
    "species": "Canine",
    "breed": "Beagle",
    "dateOfBirth": "2015-04-22T00:00:00Z",
    "dateOfRescue": "2018-02-03T00:00:00Z",
    "adopted": false,
    "weightKg": 32.7,
    "colours": [
      "cream"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 7
    },
    "vetVisit": {
      "lastVetVisit": "2018-03-16T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "active",
      "good with kids",
      "needs training"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1045",
    "name": "Harvey",
    "gender": "Male",
    "species": "Canine",
    "breed": "German Shepherd",
    "dateOfBirth": "2021-04-14T00:00:00Z",
    "dateOfRescue": "2025-04-26T08:20:39.157266Z",
    "adopted": false,
    "weightKg": 24.6,
    "colours": [
      "brown"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 22
    },
    "vetVisit": {
      "lastVetVisit": "2025-08-20T08:20:39.157266Z",
      "notes": "Routine check-up."
    },
    "tags": [
      "needs training",
      "quiet",
      "active"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1046",
    "name": "Hermione",
    "gender": "Female",
    "species": "Cat",
    "breed": "Bengal",
    "dateOfBirth": "2021-12-04T00:00:00Z",
    "dateOfRescue": "2025-04-15T08:20:39.157319Z",
    "adopted": false,
    "weightKg": 18.5,
    "colours": [
      "orange",
      "gray"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 27
    },
    "vetVisit": {
      "lastVetVisit": "2025-10-27T08:20:39.157319Z",
      "notes": "Follow-up in 3 months."
    },
    "tags": [
      "good with kids",
      "timid",
      "quiet"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      },
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1047",
    "name": "Brian",
    "gender": "Male",
    "species": "Budgie",
    "breed": "American Budgie",
    "dateOfBirth": "2020-09-09T00:00:00Z",
    "dateOfRescue": "2021-03-17T00:00:00Z",
    "adopted": false,
    "weightKg": 2.9,
    "colours": [
      "tan",
      "black"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 1
    },
    "vetVisit": {
      "lastVetVisit": "2021-09-19T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "good with kids",
      "quiet",
      "friendly",
      "needs training"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1048",
    "name": "Cruella",
    "gender": "Female",
    "species": "Budgie",
    "breed": "English Budgie",
    "dateOfBirth": "2019-10-06T00:00:00Z",
    "dateOfRescue": "2022-09-16T00:00:00Z",
    "adopted": true,
    "weightKg": 3.5,
    "colours": [
      "brown",
      "cream"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 19
    },
    "vetVisit": {
      "lastVetVisit": "2023-07-01T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "timid",
      "good with kids",
      "quiet",
      "needs training"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "distemper",
        "frequency": "for life",
        "amountMg": 5
      }
    ]
  },
  {
    "_id": "MC-1049",
    "name": "Kitty",
    "gender": "Male",
    "species": "Cat",
    "breed": "Siamese",
    "dateOfBirth": "2022-12-19T00:00:00Z",
    "dateOfRescue": "2025-03-21T08:20:39.157471Z",
    "adopted": true,
    "weightKg": 12.2,
    "colours": [
      "brown",
      "golden"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 1
    },
    "vetVisit": {
      "lastVetVisit": "2025-08-22T08:20:39.157471Z",
      "notes": "Needs dental work."
    },
    "tags": [
      "friendly",
      "timid"
    ],
    "intakeOfficer": "Liam Walsh",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      }
    ]
  },
  {
    "_id": "MC-1050",
    "name": "Puss",
    "gender": "Female",
    "species": "Cat",
    "breed": "Siamese",
    "dateOfBirth": "2016-01-24T00:00:00Z",
    "dateOfRescue": "2021-05-22T00:00:00Z",
    "adopted": true,
    "weightKg": 30.1,
    "colours": [
      "brown",
      "golden",
      "white"
    ],
    "shelterLocation": {
      "building": "St. Francis",
      "houseNumber": 14
    },
    "vetVisit": {
      "lastVetVisit": "2021-11-12T00:00:00Z",
      "notes": "Vaccinations completed."
    },
    "tags": [
      "quiet",
      "good with kids",
      "timid",
      "active"
    ],
    "intakeOfficer": "Maria Byrne",
    "vaccinations": [
      {
        "type": "rabies",
        "frequency": "yearly",
        "amountMg": 4
      },
      {
        "type": "parvovirus",
        "frequency": "yearly",
        "amountMg": 3
      }
    ]
  },
  {
    "_id": "MC-1051",
    "name": "Boffy",
    "gender": "Male",
    "species": "Canine",
    "breed": "Labrador",
    "dateOfBirth": "2017-09-25T00:00:00Z",
    "dateOfRescue": "2020-06-14T00:00:00Z",
    "adopted": false,
    "weightKg": 35.0,
    "colours": [
      "black",
      "cream",
      "orange"
    ],
    "shelterLocation": {
      "building": "St. Brigid",
      "houseNumber": 4
    },
    "vetVisit": {
      "lastVetVisit": "2021-03-29T00:00:00Z",
      "notes": "Recovered from infection."
    },
    "tags": [
      "quiet"
    ],
    "intakeOfficer": "Jane O'Connor",
    "vaccinations": [
      {
        "type": "feline leukemia",
        "frequency": "yearly",
        "amountMg": 2
      },
      {
        "type": "calicivirus",
        "frequency": "for life",
        "amountMg": 3
      }
    ]
  }
]
);