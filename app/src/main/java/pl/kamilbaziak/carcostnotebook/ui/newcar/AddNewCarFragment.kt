package pl.kamilbaziak.carcostnotebook.ui.newcar

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.TransitionInflater
import kotlinx.coroutines.launch
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.EnumUtils.getCurrencyTypeFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.getEngineTypeFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.getPetrolUnitFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.getUnitTypeFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.setEnumValuesToMaterialSpinner
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentAddNewCarBinding
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.extendedName
import pl.kamilbaziak.carcostnotebook.extra
import pl.kamilbaziak.carcostnotebook.hideKeyboard
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.name
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions

class AddNewCarFragment : Fragment(R.layout.fragment_add_new_car), MaterialAlertDialogActions {

    private val binding by lazy {
        FragmentAddNewCarBinding.inflate(layoutInflater)
    }
    private val viewModel: AddNewCarViewModel by inject()
    private val car by extra<Car?>(EXTRA_CAR)
    private val title by extra<String>(EXTRA_TITLE)
    private val dateDialog =
        MaterialDatePicker.Builder.datePicker().setTitleText(R.string.choose_date_when_bought)
            .build()
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleOnBack()
        }
    }

    // Store pending scroll operations to prevent memory leaks
    private val pendingScrollRunnables = mutableListOf<Runnable>()

    // Store original padding to preserve it when keyboard appears/disappears
    private var originalBottomPadding = 0

    // Reusable buffer for location calculations to avoid allocations
    private val locationBuffer = IntArray(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        // Store original padding before applying insets (only if not already set)
        // This preserves the value across configuration changes
        if (originalBottomPadding == 0) {
            originalBottomPadding = nestedScrollView.paddingBottom
        }

        // Handle window insets for edge-to-edge
        setupWindowInsets()

        sectionCarData.textDivider.text = getString(R.string.car_data)
        sectionCarWhenBought.textDivider.text = getString(R.string.car_data_when_bought)
        sectionNonEditableItems.textDivider.text = getString(R.string.non_editable_data)

        toolbar.apply {
            title = car?.name() ?: getString(R.string.add_new_car)
            setNavigationOnClickListener {
                handleOnBack()
            }
            menu
                .add(R.string.save)
                .setIcon(R.drawable.ic_done)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    saveCar()
                    true
                }
        }

        car?.let { car ->
            viewModel.apply {
                getAllOdometer(car)
                getLastOdometer(car)
                car.dateWhenBought?.let {
                    changePickedDate(it)
                }
            }

            editMode(car)
        }

        disableViewsWhenEditMode()

        dateDialog.addOnPositiveButtonClickListener {
            viewModel.changePickedDate(it)
        }

        activity?.actionBar?.title = title

        viewModel.apply {
            pickedDate.observe(viewLifecycleOwner) {
                textInputCalendarWhenBought.editText?.setText(it.toDate())
            }
            odometerAll.observe(viewLifecycleOwner) {
                textInputCarOdometer.isEnabled = it != null && it.size > 1
            }
            lastOdometer.observe(viewLifecycleOwner) {
                if (it != null) {
                    textInputCarOdometer.editText?.setText(it.input.toTwoDigits())
                    textInputUnit.editText?.setText(it.unit.name)
                }
            }
        }

        setEnumValuesToMaterialSpinner(textInputEngineType.editText as MaterialAutoCompleteTextView,
            EngineEnum.entries.map { it.name })

        setEnumValuesToMaterialSpinner(textInputPetrolUnit.editText as MaterialAutoCompleteTextView,
            PetrolUnitEnum.entries.map { it.name })

        setEnumValuesToMaterialSpinner(textInputUnit.editText as MaterialAutoCompleteTextView,
            UnitEnum.entries.map { it.name })

        setEnumValuesToMaterialSpinner(textInputCurrency.editText as MaterialAutoCompleteTextView,
            CurrencyEnum.entries.map { it.extendedName(requireContext()) })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addNewCarEvent.collect { event ->
                    when (event) {
                        AddNewCarViewModel.AddNewCarEvent.NavigateBack ->
                            requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }

        textInputCarOdometer.editText?.setOnClickListener { this@AddNewCarFragment.hideKeyboard() }
        textInputUnit.editText?.setOnClickListener { this@AddNewCarFragment.hideKeyboard() }
        textInputEngineType.editText?.setOnClickListener { this@AddNewCarFragment.hideKeyboard() }
        textInputPetrolUnit.editText?.setOnClickListener { this@AddNewCarFragment.hideKeyboard() }
        textInputCurrency.editText?.setOnClickListener { this@AddNewCarFragment.hideKeyboard() }
        textInputCalendarWhenBought.editText?.setOnClickListener {
            dateDialog.show(childFragmentManager, DATE_PICKER_TAG)
            this@AddNewCarFragment.hideKeyboard()
        }

        // Setup keyboard handling - scroll to focused field
        setupKeyboardHandling()

        return@run
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel all pending scroll operations to prevent memory leaks
        pendingScrollRunnables.forEach { binding.root.removeCallbacks(it) }
        pendingScrollRunnables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private fun disableViewsWhenEditMode() = binding.run {
        val editMode = car != null
        textInputCarOdometer.isEnabled = !editMode
        textInputPetrolUnit.isEnabled = !editMode
        textInputEngineType.isEnabled = !editMode
        textInputUnit.isEnabled = !editMode
        textInputCurrency.isEnabled = !editMode
    }

    private fun editMode(car: Car) = binding.apply {
        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
        toolbar.setNavigationIconTint(ContextCompat.getColor(requireContext(),R.color.md_theme_error))
        layoutNonEditable.apply {
            sectionNonEditableItems.root.isVisible = false
            setPadding(0, 0, 0, 0)
            setBackgroundColor(Color.TRANSPARENT)
        }
        textInputCarBrand.editText?.setText(car.brand)
        textInputCarModel.editText?.setText(car.model)
        textInputCarYear.editText?.setText(car.year.toString())
        textInputCarLicencePlate.editText?.setText(car.licensePlate)
        textInputEngineType.editText?.setText(car.engineEnum.name)
        textInputPetrolUnit.editText?.setText(car.petrolUnit.name)
        textInputCurrency.editText?.setText(car.currency.extendedName(requireContext()))
        textInputDescription.editText?.setText(car.description)
        car.priceWhenBought?.let {
            textInputCarPriceWhenBought.editText?.setText(it.toTwoDigits())
        }
        car.dateWhenBought?.let {
            textInputCalendarWhenBought.editText?.setText(it.toDate())
        }
    }

    private fun saveCar() {
        if (!validateData()) return

        binding.apply {
            if (car != null) {
                viewModel.updateCar(
                    car!!.copy(
                        brand = textInputCarBrand.editText?.text.toString(),
                        model = textInputCarModel.editText?.text.toString(),
                        year = textInputCarYear.editText?.text.toString().toInt(),
                        licensePlate = textInputCarLicencePlate.editText?.text.toString(),
                        dateWhenBought = viewModel.pickedDate.value,
                        priceWhenBought = textInputCarPriceWhenBought.editText?.text.toString()
                            .toDoubleOrNull(),
                        description = textInputDescription.editText?.text.toString()
                    ),
                    viewModel.lastOdometer.value,
                    textInputCarOdometer.editText?.text.toString().toDouble()
                )
            } else {
                viewModel.addCar(
                    Car(
                        0,
                        textInputCarBrand.editText?.text.toString(),
                        textInputCarModel.editText?.text.toString(),
                        textInputCarYear.editText?.text.toString().toInt(),
                        textInputCarLicencePlate.editText?.text.toString(),
                        getEngineTypeFromName(textInputEngineType.editText?.text.toString()),
                        getPetrolUnitFromName(textInputPetrolUnit.editText?.text.toString()),
                        getUnitTypeFromName(textInputUnit.editText?.text.toString()),
                        textInputDescription.editText?.text.toString(),
                        textInputCarPriceWhenBought.editText?.text.toString().toDoubleOrNull(),
                        viewModel.pickedDate.value,
                        getCurrencyTypeFromName(
                            textInputCurrency.editText?.text.toString(), requireContext()
                        )
                    ), textInputCarOdometer.editText?.text.toString().toDouble(),
                    textInputCarOdometerWhenBought.editText?.text.toString().toDoubleOrNull()
                )
            }
        }
    }

    private fun handleOnBack() = binding.run {
        val canGoBack = if (car != null) {
            !checkIfDataChanged(
                car!!,
                textInputCarBrand.editText?.text.toString(),
                textInputCarModel.editText?.text.toString(),
                textInputCarYear.editText?.text.toString(),
                textInputCarLicencePlate.editText?.text.toString(),
                viewModel.pickedDate.value!!,
                textInputCarPriceWhenBought.editText?.text.toString(),
                textInputDescription.editText?.text.toString()
            )
        } else {
            checkIfEssentialDataEmpty()
        }

        if (canGoBack) {
            goBack()
        } else {
            MaterialAlertDialog.show(
                childFragmentManager,
                getString(R.string.unsaved_data),
                getString(R.string.do_you_want_to_discard_changes),
                getString(R.string.discard_changes)
            )
        }
    }

    private fun checkIfDataChanged(
        car: Car,
        carBrand: String,
        carModel: String,
        productionYear: String,
        licencePlate: String,
        dateWhenBought: Long,
        carPrice: String,
        description: String
    ) = carBrand != car.brand ||
            carModel != car.model ||
            productionYear.toInt() != car.year ||
            licencePlate != car.licensePlate ||
            dateWhenBought.toInt() != car.year ||
            carPrice.toDoubleOrNull() != car.priceWhenBought ||
            description != car.description

    private fun checkIfEssentialDataEmpty(): Boolean = binding.run {
        return textInputCarBrand.editText?.text.toString().isEmpty() &&
                textInputCarModel.editText?.text.toString().isEmpty() &&
                textInputCarOdometer.editText?.text.toString().isEmpty() &&
                textInputEngineType.editText?.text.toString().isEmpty() &&
                textInputPetrolUnit.editText?.text.toString().isEmpty() &&
                textInputUnit.editText?.text.toString().isEmpty() &&
                textInputCurrency.editText?.text.toString().isEmpty()
    }

    private fun validateData(): Boolean {
        resetTextInputErrors()
        binding.apply {
            if (textInputCarBrand.editText?.text.toString().isEmpty()) {
                textInputCarBrand.error = getString(R.string.insert_car_brand)
            } else if (textInputCarModel.editText?.text.toString().isEmpty()) {
                textInputCarModel.error = getString(R.string.insert_car_model)
            } else if (textInputCarYear.editText?.text.toString().isEmpty()) {
                textInputCarYear.error = getString(R.string.insert_production_year)
            } else if (textInputCarOdometer.editText?.text.toString().isEmpty()) {
                textInputCarOdometer.error = getString(R.string.insert_car_mileage)
            } else if (textInputUnit.editText?.text.toString().isEmpty()) {
                textInputUnit.error = getString(R.string.choose_odometer_unit)
            } else if (textInputEngineType.editText?.text.toString().isEmpty()) {
                textInputEngineType.error = getString(R.string.choose_petrol_type)
            } else if (textInputPetrolUnit.editText?.text.toString().isEmpty()) {
                textInputPetrolUnit.error = getString(R.string.choose_petrol_unit)
            } else if (textInputCurrency.editText?.text.toString().isEmpty()) {
                textInputCurrency.error = getString(R.string.choose_currency)
            } else {
                return true
            }
            return false
        }
    }

    private fun resetTextInputErrors() = binding.run {
        textInputCarBrand.error = null
        textInputCarModel.error = null
        textInputCarOdometer.error = null
        textInputEngineType.error = null
        textInputPetrolUnit.error = null
        textInputUnit.error = null
        textInputCurrency.error = null
        textInputCarYear.error = null
    }

    private fun goBack() {
        onBackPressedCallback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    /**
     * Sets up WindowInsets listener to handle edge-to-edge display.
     *
     * This function:
     * - Applies top padding to AppBarLayout to avoid status bar overlap
     * - Applies bottom padding to NestedScrollView to avoid keyboard overlap
     * - Preserves original padding from XML layout
     */
    private fun setupWindowInsets() = binding.run {
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Apply top padding to AppBarLayout for status bar
            appBarLayout.setPadding(0, systemBars.top, 0, 0)

            // Apply bottom padding to NestedScrollView for keyboard
            // Preserve original padding and add keyboard height
            nestedScrollView.setPadding(
                nestedScrollView.paddingLeft,
                nestedScrollView.paddingTop,
                nestedScrollView.paddingRight,
                originalBottomPadding + ime.bottom
            )

            insets
        }
    }

    /**
     * Sets up automatic scrolling when EditText receives focus.
     *
     * This function:
     * - Listens for focus changes on all input fields
     * - Cancels any pending scroll operations to prevent race conditions
     * - Scrolls the focused field into view with padding from top
     * - Ensures the field is visible above the keyboard
     */
    private fun setupKeyboardHandling() = binding.run {
        val editTexts = listOf(
            textInputCarBrand.editText,
            textInputCarModel.editText,
            textInputCarYear.editText,
            textInputCarLicencePlate.editText,
            textInputCarPriceWhenBought.editText,
            textInputDescription.editText
        )

        editTexts.forEach { editText ->
            editText?.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // Cancel previous scroll operations to prevent race conditions
                    cancelPendingScrolls()

                    // Create runnable for delayed scroll operation
                    val scrollRunnable = object : Runnable {
                        override fun run() {
                            // Safety check: ensure views are still attached to window
                            if (!view.isAttachedToWindow || !nestedScrollView.isAttachedToWindow) {
                                pendingScrollRunnables.remove(this)
                                return
                            }

                            // Get the position of the focused view using reusable buffer
                            nestedScrollView.getLocationOnScreen(locationBuffer)
                            val scrollViewY = locationBuffer[1]

                            view.getLocationOnScreen(locationBuffer)
                            val viewY = locationBuffer[1]

                            // Calculate the relative position
                            val relativeTop = viewY - scrollViewY + nestedScrollView.scrollY

                            // Scroll so the field is visible with padding from top
                            val targetScroll = relativeTop - SCROLL_PADDING_DP.dpToPx()

                            nestedScrollView.smoothScrollTo(0, targetScroll.coerceAtLeast(0))

                            // Remove from pending list after execution
                            pendingScrollRunnables.remove(this)
                        }
                    }

                    // Store runnable to allow cleanup
                    pendingScrollRunnables.add(scrollRunnable)

                    // Delay to wait for keyboard to appear and insets to be applied
                    view.postDelayed(scrollRunnable, KEYBOARD_ANIMATION_DELAY_MS)
                }
            }
        }
    }

    /**
     * Cancels all pending scroll operations.
     * Used to prevent race conditions when user quickly switches between fields.
     */
    private fun cancelPendingScrolls() {
        pendingScrollRunnables.forEach { binding.root.removeCallbacks(it) }
        pendingScrollRunnables.clear()
    }

    /**
     * Converts density-independent pixels (DP) to actual pixels (PX).
     *
     * @return The pixel value based on the current screen density
     */
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    companion object Factory {

        const val DATE_PICKER_TAG = "AddNewCarFragment.DATE_PICKER_TAG"
        const val TAG = "AddNewCarFragment"
        private const val EXTRA_CAR = "EXTRA_CAR"
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        // Keyboard handling constants
        /** Delay in milliseconds to wait for keyboard animation to complete */
        private const val KEYBOARD_ANIMATION_DELAY_MS = 300L
        /** Padding in DP from top of screen when scrolling to focused field */
        private const val SCROLL_PADDING_DP = 64

        fun newInstance(car: Car?, title: String) = AddNewCarFragment().apply {
            arguments = car?.let { Pair(EXTRA_CAR, it) }?.let {
                bundleOf(
                    EXTRA_TITLE to title, it
                )
            }
        }
    }

    override fun onPositiveButtonClicked() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onNegativeButtonClicked() { }

    override fun getItemListItemTitle(title: String) { }
}
