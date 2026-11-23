use("eventlogdb")
/* resources */

db.resources.find({type:'Windows 11'});
db.resources.find({active:true});

/*actors*/
db.actors.find({'permission.type':'admin'});
db.actors.find({tags:'Cork'});