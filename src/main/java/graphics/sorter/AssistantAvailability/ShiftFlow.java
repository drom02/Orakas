package graphics.sorter.AssistantAvailability;

import graphics.sorter.Assistant;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ShiftFlow extends TextFlow {

    public AssistantAvailability getShiftAvailability() {
        return shiftAvailability;
    }

    public void setShiftAvailability(AssistantAvailability assistantAvailability) {
        this.shiftAvailability = assistantAvailability;
    }

    private AssistantAvailability shiftAvailability;

    public ShiftFlow(AssistantAvailability assistantAvailability){
        setShiftAvailability(assistantAvailability);
    }
    public void displayContent(HashMap<UUID, Assistant> index){
        if(getShiftAvailability()!=null){
            Assistant a = index.get(getShiftAvailability().getAssistant());
            String time = Arrays.toString(shiftAvailability.getAvailability().getStart()) + " " + Arrays.toString(shiftAvailability.getAvailability().getEnd());
            if(!this.getChildren().isEmpty()){
                Text t = (Text) this.getChildren().get(0);
                t.setText(a.getName() + " " + a.getSurname()+ time);
            }else{
                this.getChildren().add(new Text(a.getName() + " " + a.getSurname()+ time));
            }

        }
    }

}
