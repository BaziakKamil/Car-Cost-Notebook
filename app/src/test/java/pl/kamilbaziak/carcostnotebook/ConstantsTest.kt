package pl.kamilbaziak.carcostnotebook

import org.junit.Test
import kotlin.test.assertEquals

class ConstantsTest {

    @Test
    fun `table names are correct`() {
        assertEquals("car_table", Constants.CAR_TABLE)
        assertEquals("maintenance_table", Constants.MAINTENANCE_TABLE)
        assertEquals("tank_fill_table", Constants.TANK_FILL_TABLE)
        assertEquals("odometer_table", Constants.ODOMETER_TABLE)
    }

    @Test
    fun `date formats are correct`() {
        assertEquals("dd MMMM yyyy", Constants.DATE_FORMAT)
        assertEquals("dd-MM-yyyy_hh-mm-ss", Constants.BACKUP_DATE_FORMAT)
        assertEquals("hh:mm", Constants.TIME_FORMAT)
    }

    @Test
    fun `backup constants are correct`() {
        assertEquals("carNotebookBackup_", Constants.BACKUP_NAME)
        assertEquals("Car Cost Notebook Backup", Constants.BACKUP_DIRECTORY)
        assertEquals(".ccn", Constants.BACKUP_EXTENSION)
        assertEquals("/|/", Constants.BACKUP_SEPARATOR)
    }
}

