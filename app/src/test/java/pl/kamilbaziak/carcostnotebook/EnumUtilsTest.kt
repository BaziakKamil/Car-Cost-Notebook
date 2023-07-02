package pl.kamilbaziak.carcostnotebook

import org.junit.Test
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import kotlin.test.assertEquals

internal class EnumUtilsTest {

    @Test
    fun `get engine type from it's name`() {
        assertEquals(
            EngineEnum.Diesel,
            EnumUtils.getEngineTypeFromName("Diesel")
        )
    }

    @Test
    fun `get engine type from it's name return default value`() {
        assertEquals(
            EngineEnum.Petrol,
            EnumUtils.getEngineTypeFromName("False value")
        )
    }

    @Test
    fun `get petrol unit from it's name`() {
        assertEquals(
            PetrolUnitEnum.Galon,
            EnumUtils.getPetrolUnitFromName("Galon")
        )
    }

    @Test
    fun `get petrol unit from it's name return default value`() {
        assertEquals(
            PetrolUnitEnum.Liter,
            EnumUtils.getPetrolUnitFromName("false value")
        )
    }

    @Test
    fun `get petrol type from it's name`() {
        assertEquals(
            PetrolEnum.CNG,
            EnumUtils.getPetrolEnumFromName("CNG")
        )
    }

    @Test
    fun `get petrol type from it's name return default value`() {
        assertEquals(
            PetrolEnum.Petrol,
            EnumUtils.getPetrolEnumFromName("false value")
        )
    }

    @Test
    fun `get unit type from it's name`() {
        assertEquals(
            UnitEnum.Miles,
            EnumUtils.getUnitTypeFromName("Miles")
        )
    }

    @Test
    fun `get unit type from it's name return default value`() {
        assertEquals(
            UnitEnum.Kilometers,
            EnumUtils.getUnitTypeFromName("false value")
        )
    }
}