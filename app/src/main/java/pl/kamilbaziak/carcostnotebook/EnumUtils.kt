package pl.kamilbaziak.carcostnotebook

import com.google.android.material.textfield.MaterialAutoCompleteTextView
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

object EnumUtils {

    fun getEngineTypeFromName(name: String?): EngineEnum = name?.let { engineType ->
        EngineEnum.values().find {
            it.name == engineType
        }
    } ?: EngineEnum.Petrol

    fun getPetrolUnitFromName(name: String?): PetrolUnitEnum = name?.let { petrol ->
        PetrolUnitEnum.values().find {
            it.name == petrol
        }
    } ?: PetrolUnitEnum.Liter

    fun getPetrolEnumFromName(name: String?): PetrolEnum = name?.let { petrol ->
        PetrolEnum.values().find {
            it.name == petrol
        }
    } ?: PetrolEnum.Petrol

    fun getUnitTypeFromName(name: String?): UnitEnum = name?.let { unit ->
        UnitEnum.values().find {
            it.name == unit
        }
    } ?: UnitEnum.Kilometers

    fun setEnumValuesToMaterialSpinner(
        view: MaterialAutoCompleteTextView,
        list: List<String>
    ) = view.setSimpleItems(list.toTypedArray())
}
