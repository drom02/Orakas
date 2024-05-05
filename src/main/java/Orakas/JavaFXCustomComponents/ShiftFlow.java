package Orakas.JavaFXCustomComponents;

import Orakas.Humans.Assistant;
import Orakas.AssistantAvailability.AssistantAvailability;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
/*
Extended TextFlow for use in shiftViewer. Always includes information about contained assistantAvailability
 */
public class ShiftFlow extends TextFlow {

    public Orakas.AssistantAvailability.AssistantAvailability getAssistantAvailability() {
        return AssistantAvailability;
    }

    public void setAssistantAvailability(AssistantAvailability assistantAvailability) {
        this.AssistantAvailability = assistantAvailability;
    }

    private AssistantAvailability AssistantAvailability;

    public ShiftFlow(AssistantAvailability assistantAvailability){
        setAssistantAvailability(assistantAvailability);
    }
    public void displayContent(HashMap<UUID, Assistant> index){
        if(getAssistantAvailability()!=null){
            Assistant a = index.get(getAssistantAvailability().getAssistant());
            String time = Arrays.toString(AssistantAvailability.getAvailability().getStart()) + " " + Arrays.toString(AssistantAvailability.getAvailability().getEnd());
            if(!this.getChildren().isEmpty()){
                Text t = (Text) this.getChildren().get(0);
                t.setText(a.getName() + " " + a.getSurname()+ time);
            }else{
                this.getChildren().add(new Text(a.getName() + " " + a.getSurname()+ time));
            }

        }
    }

}
