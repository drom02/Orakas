package graphics.sorter.Mediator;

public class ReferentialBoolean {
    /*
    0=default state
    1 = negative solution
    2= positive solution
     */
        private int value;

        public ReferentialBoolean(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

}
