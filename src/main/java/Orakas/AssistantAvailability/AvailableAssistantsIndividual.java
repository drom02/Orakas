package Orakas.AssistantAvailability;

import Orakas.Structs.Availability.AvailableAssistants;

import java.util.UUID;
/*
Available assistants, but instead of representing all assistants, represents only one.
 */
public class AvailableAssistantsIndividual extends AvailableAssistants {
    public UUID getAssistant() {
        return assistant;
    }
    public void setAssistant(UUID assistant) {
        this.assistant = assistant;
    }
    private UUID assistant;
    public AvailableAssistantsIndividual(UUID assistant,int year, int month){
        setAssistant(assistant);
        setYear(year);
        setMonth(month);
    }
}
