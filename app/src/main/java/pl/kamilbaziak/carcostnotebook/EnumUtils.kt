package pl.kamilbaziak.carcostnotebook

import android.content.Context
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

object EnumUtils {

    fun getEngineTypeFromName(name: String?): EngineEnum = name?.let { engineType ->
        EngineEnum.entries.find {
            it.name == engineType
        }
    } ?: EngineEnum.Petrol

    fun getPetrolUnitFromName(name: String?): PetrolUnitEnum = name?.let { petrol ->
        PetrolUnitEnum.entries.find {
            it.name == petrol
        }
    } ?: PetrolUnitEnum.Liter

    fun getPetrolEnumFromName(name: String?): PetrolEnum = name?.let { petrol ->
        PetrolEnum.entries.find {
            it.name == petrol
        }
    } ?: PetrolEnum.Petrol

    fun getUnitTypeFromName(name: String?): UnitEnum = name?.let { unit ->
        UnitEnum.entries.find {
            it.name == unit
        }
    } ?: UnitEnum.Kilometers

    fun getCurrencyTypeFromName(name: String?, context: Context): CurrencyEnum = name?.let { currency ->
        CurrencyEnum.entries.find {
            it.extendedName(context) == name
        }
    } ?: CurrencyEnum.Zloty

    fun setEnumValuesToMaterialSpinner(
        view: MaterialAutoCompleteTextView,
        list: List<String>
    ) = view.setSimpleItems(list.toTypedArray())
}
