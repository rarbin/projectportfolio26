use("eventlogdb")

/*
tracks metadata on resources (user sessions, virtual machines, cloud storage). 
any 'event' is ACTED ON a 'resource'
*/
//https://www.mongodb.com/docs/manual/reference/resource-document/

/*CREATE*/
db.resources.insertMany([
   {
      /*USER SESSION*/
      /*https://mayukh551.hashnode.dev/sessions-and-transactions-in-mongodb, https://www.mongodb.com/docs/manual/reference/method/session/#mongodb-method-Session */
      "_id": "Sess-A",
      "type": "session",
      "metadata": [
         {
            "success": true,
            "authMethod": "2FA",
            //https://stackoverflow.com/a/71492643 
            "user": "User-A"
         }
      ]
   },
   {
      /*VIRTUAL MACHINES*/
      /*could be based off Azure or AWS cloud details*/
      "_id": "VM-A",
      "type": "Windows 11",
      "active": false,
      "RAM": "16GB",
      "Storage": "50GB" //ref cloud tbd
   },
   {
      "_id": "VM-B",
      "type": "Ubuntu",
      "active": true,
      "RAM": "8GB",
      "Storage": "25GB" //ref cloud tbd
   },
   {
      /*CLOUD STORAGE*/
      "_id": "Cloud-A",
      "size": "50GB",
      "metadata": [
         {
            "Writable": true,
            "fileType": "ISO",
            "FreeSpace": "10.3GB"
         }
      ]
   },
   {
      "_id": "Cloud-B",
      "size": "25GB",
      "metadata": [
         {
            "Writable": true,
            "fileType": "ISO",
            "FreeSpace": "4.3GB"
         }
      ]
   }
])

/*READ*/
//will also keep to queries

/*UPDATE*/
db.resources.updateOne(
   { _id: "VM-A" },
   { $set: { active: true } }
)

/*add a metadata field to Cloud-A*/
db.resources.updateOne(
   { _id: "Cloud-A" },
   { $set: { "metadata.0.encrypted": true } }
)

/*DELETE*/

/* 
db.resources.deleteOne({ 
   _id: "VM-B" 
})
*/

/* db.resources.deleteMany({ 
   type: "session" 
})

*/
/* AGGREGATE */ 

