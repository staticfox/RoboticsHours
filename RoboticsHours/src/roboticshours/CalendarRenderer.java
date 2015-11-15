package roboticshours;

import java.util.Calendar;
import javax.swing.table.DefaultTableCellRenderer;


class CalendarRenderer extends DefaultTableCellRenderer {
        public CalendarRenderer() {
            super();
            this.setHorizontalAlignment(CENTER);
        }

        @Override
        public void setValue(Object value) {
            Calendar date = (Calendar)value;
            setText(date.get(Calendar.MONTH) + 1 + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.YEAR)));
        }
    }
