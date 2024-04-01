package Orakas.AssistantAvailability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class AssistantAvailability {
    private UUID assistant;



    private Availability availability;
@JsonCreator
    public AssistantAvailability(@JsonProperty("assistant")UUID assistant,@JsonProperty("availability")Availability availability){
        setAssistant(assistant);
        setAvailability(availability);
    }
    public UUID getAssistant() {
        return assistant;
    }

    public void setAssistant(UUID assistant) {
        this.assistant = assistant;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
}
