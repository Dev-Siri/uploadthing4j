module uploadthing4j {
    exports dev.siri.uploadthing;
    exports dev.siri.uploadthing.models;
    exports dev.siri.uploadthing.dto.requests;
    exports dev.siri.uploadthing.dto.responses;
    exports dev.siri.uploadthing.models.errors;

    requires async.http.client;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires io.netty.buffer;
    requires org.slf4j;

    opens dev.siri.uploadthing.dto.responses to com.google.gson;
    opens dev.siri.uploadthing.dto.requests to com.google.gson;
    opens dev.siri.uploadthing.models to com.google.gson;
}