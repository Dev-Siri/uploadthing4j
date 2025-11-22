module uploadthing4j {
    exports dev.siri.uploadthing;
    exports dev.siri.uploadthing.models;
    exports dev.siri.uploadthing.models.errors;

    requires async.http.client;
    requires com.google.gson;
    requires org.jetbrains.annotations;
}