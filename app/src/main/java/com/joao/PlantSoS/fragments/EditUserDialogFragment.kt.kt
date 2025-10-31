package com.joao.PlantSoS.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.joao.PlantSoS.R
import com.joao.PlantSoS.models.User
import com.joao.PlantSoS.viewmodel.ManageUsersViewModel

class EditUserDialogFragment : DialogFragment() {

    private val viewModel: ManageUsersViewModel by activityViewModels()
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pega o objeto User passado como argumento
        user = arguments?.getParcelable("USER") ?: run {
            Toast.makeText(context, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_edit_user, null)

        builder.setView(view)

        // Encontra os componentes dentro do layout do diálogo
        val etName = view.findViewById<EditText>(R.id.et_user_name)
        val etEmail = view.findViewById<EditText>(R.id.et_user_email)
        val etPassword = view.findViewById<EditText>(R.id.et_user_password)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinner_user_role)
        val btnEdit = view.findViewById<TextView>(R.id.btn_edit_user)
        val layoutActionButtons = view.findViewById<LinearLayout>(R.id.layout_action_buttons)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        // Função para popular os dados
        fun populateData() {
            etName.setText(user.name)
            etEmail.setText(user.email)
            val roles = arrayOf("COMMON", "ADMIN")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
            spinnerRole.adapter = adapter
            spinnerRole.setSelection(roles.indexOf(user.userType))
        }

        // Função para controlar o modo de edição
        fun setEditMode(isEditing: Boolean) {
            etName.isEnabled = isEditing
            etEmail.isEnabled = isEditing
            etPassword.isEnabled = isEditing
            spinnerRole.isEnabled = isEditing
            btnEdit.visibility = if (isEditing) View.GONE else View.VISIBLE
            layoutActionButtons.visibility = if (isEditing) View.VISIBLE else View.GONE
        }

        // Configuração inicial
        populateData()
        setEditMode(false)

        // Configuração dos cliques
        btnEdit.setOnClickListener { setEditMode(true) }
        btnCancel.setOnClickListener {
            populateData()
            setEditMode(false)
        }
        btnSave.setOnClickListener {
            val newName = etName.text.toString()
            val newEmail = etEmail.text.toString()
            val newPassword = etPassword.text.toString()
            val newRole = spinnerRole.selectedItem.toString()
            viewModel.updateUser(user.id, newName, newEmail, if (newPassword.isNotBlank()) newPassword else null, newRole)
            dismiss()
        }

        return builder.create()
    }

    companion object {
        fun newInstance(user: User): EditUserDialogFragment {
            val fragment = EditUserDialogFragment()
            val args = Bundle()
            args.putParcelable("USER", user)
            fragment.arguments = args
            return fragment
        }
    }
}