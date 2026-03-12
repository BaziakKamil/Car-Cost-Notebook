package pl.kamilbaziak.carcostnotebook

import org.junit.Test
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExtensionsTest {

    @Test
    fun `String empty returns empty string`() {
        assertEquals("", String.empty())
    }

    @Test
    fun `String space returns space string`() {
        assertEquals(" ", String.space())
    }

    @Test
    fun `hasLetters returns true for string with uppercase letters`() {
        assertTrue("ABC123".hasLetters())
    }

    @Test
    fun `hasLetters returns true for string with lowercase letters`() {
        assertTrue("abc123".hasLetters())
    }

    @Test
    fun `hasLetters returns true for string with mixed case letters`() {
        assertTrue("AbC123".hasLetters())
    }

    @Test
    fun `hasLetters returns false for string with only numbers`() {
        assertFalse("123456".hasLetters())
    }

    @Test
    fun `hasLetters returns false for string with only special characters`() {
        assertFalse("!@#$%^".hasLetters())
    }

    @Test
    fun `hasLetters returns false for empty string`() {
        assertFalse("".hasLetters())
    }

    @Test
    fun `UnitEnum shortcut returns km for Kilometers`() {
        assertEquals("km", UnitEnum.Kilometers.shortcut())
    }

    @Test
    fun `UnitEnum shortcut returns mi for Miles`() {
        assertEquals("mi", UnitEnum.Miles.shortcut())
    }

    @Test
    fun `PetrolUnitEnum shortcut returns l for Liter`() {
        assertEquals("l", PetrolUnitEnum.Liter.shortcut())
    }

    @Test
    fun `PetrolUnitEnum shortcut returns gal for Galon`() {
        assertEquals("gal", PetrolUnitEnum.Galon.shortcut())
    }

    @Test
    fun `PetrolUnitEnum shortcut returns kWh for kWh`() {
        assertEquals("kWh", PetrolUnitEnum.kWh.shortcut())
    }

    @Test
    fun `PetrolUnitEnum shortcut returns kg for kg`() {
        assertEquals("kg", PetrolUnitEnum.kg.shortcut())
    }

    @Test
    fun `Double toTwoDigits formats correctly with two decimal places`() {
        assertEquals("123.45", 123.45.toTwoDigits())
    }

    @Test
    fun `Double toTwoDigits formats correctly with one decimal place`() {
        assertEquals("123.4", 123.4.toTwoDigits())
    }

    @Test
    fun `Double toTwoDigits formats correctly with no decimal places`() {
        assertEquals("123", 123.0.toTwoDigits())
    }

    @Test
    fun `Double toTwoDigits rounds correctly`() {
        assertEquals("123.46", 123.456.toTwoDigits())
    }

    @Test
    fun `Double toTwoDigits handles zero`() {
        assertEquals("0", 0.0.toTwoDigits())
    }

    @Test
    fun `Car name returns brand and model`() {
        val car = Car(
            id = 1,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = EngineEnum.Petrol,
            petrolUnit = PetrolUnitEnum.Liter,
            unit = UnitEnum.Kilometers,
            description = "Test car",
            currency = CurrencyEnum.Zloty
        )
        assertEquals("Toyota Corolla", car.name())
    }

    @Test
    fun `Car name handles empty brand`() {
        val car = Car(
            id = 1,
            brand = "",
            model = "Corolla",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = EngineEnum.Petrol,
            petrolUnit = PetrolUnitEnum.Liter,
            unit = UnitEnum.Kilometers,
            description = "Test car",
            currency = CurrencyEnum.Zloty
        )
        assertEquals(" Corolla", car.name())
    }

    @Test
    fun `Car name handles empty model`() {
        val car = Car(
            id = 1,
            brand = "Toyota",
            model = "",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = EngineEnum.Petrol,
            petrolUnit = PetrolUnitEnum.Liter,
            unit = UnitEnum.Kilometers,
            description = "Test car",
            currency = CurrencyEnum.Zloty
        )
        assertEquals("Toyota ", car.name())
    }
}

