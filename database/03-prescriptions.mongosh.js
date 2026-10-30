// Run after the MySQL lab data has been loaded. Synthetic lab records only.
const target = db.getSiblingDB("prescriptions");
const documents = EJSON.parse(require("fs").readFileSync("database/prescriptions.json", "utf8"));
// Stable IDs make reruns safe; existing records are not overwritten.
for (const document of documents) {
  target.prescriptions.updateOne({_id: document._id}, {$setOnInsert: document}, {upsert: true});
}
target.prescriptions.createIndex({appointmentId: 1});
print("Prescription count: " + target.prescriptions.countDocuments({}));
target.prescriptions.find().sort({_id: 1}).limit(5).forEach(doc => printjson(doc));
