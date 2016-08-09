package org.age.zk.services.communication.message;

import org.junit.Test;

import static org.age.zk.services.AgeZKStarterAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;


public class MessageSerializerTest {

    @Test
    public void convertNullJSON() {
        Message convert = MessageSerializer.deserialize((String) null);
        assertThat(convert).isNull();
    }

    @Test
    public void convertEmptyJSON() {
        Message convert = MessageSerializer.deserialize("");
        assertThat(convert).isNull();
    }

    @Test
    public void convertNullMessage() {
        String convert = MessageSerializer.serialize((Message) null);
        assertThat(convert).isEqualTo("null");
    }


    @Test
    public void convertMessageToJSON() {
        Message message = new Message("sender", "recipient", "UUID", 123L, "body");
        String json = MessageSerializer.serialize(message);

        System.out.println(json);

        assertThat(json).isNotNull();
    }

    @Test
    public void convertJSONToMessage() {
        String json = "{\n" +
                "  \"senderId\": \"alex\",\n" +
                "  \"recipientId\": \"kate\",\n" +
                "  \"messageUUID\": \"as213\",\n" +
                "  \"sendTime\": 555,\n" +
                "  \"body\": \"great text\"\n" +
                "}";
        Message convert = MessageSerializer.deserialize(json);

        assertThat(convert)
                .isNotNull()
                .hasSenderId("alex")
                .hasRecipientId("kate")
                .hasMessageUUID("as213")
                .hasSendTime(555)
                .hasBody("great text");

    }
}