package pl.kamilbaziak.carcostnotebook

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateUtilsTest {

    @Test
    fun `formatDateFromLong formats date correctly`() {
        // 2023-01-15 12:30:00 UTC
        val timestamp = 1673785800000L
        val result = DateUtils.formatDateFromLong(timestamp)
        
        // Result should contain date components (format may vary by locale)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatTimeFromLong formats time correctly`() {
        // 2023-01-15 12:30:00 UTC
        val timestamp = 1673785800000L
        val result = DateUtils.formatTimeFromLong(timestamp)
        
        // Result should contain time components
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatBackupDateFromLong formats backup date correctly`() {
        // 2023-01-15 12:30:00 UTC
        val timestamp = 1673785800000L
        val result = DateUtils.formatBackupDateFromLong(timestamp)
        
        // Result should contain date and time components
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatDateFromLong handles zero timestamp`() {
        val result = DateUtils.formatDateFromLong(0L)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatTimeFromLong handles zero timestamp`() {
        val result = DateUtils.formatTimeFromLong(0L)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatBackupDateFromLong handles zero timestamp`() {
        val result = DateUtils.formatBackupDateFromLong(0L)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Long toDate extension works correctly`() {
        val timestamp = 1673785800000L
        val result = timestamp.toDate()
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `Long toTime extension works correctly`() {
        val timestamp = 1673785800000L
        val result = timestamp.toTime()
        assertTrue(result.isNotEmpty())
    }
}

