package com.joao.plantdoctor.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.joao.plantdoctor.R
import com.joao.plantdoctor.models.User
import com.joao.plantdoctor.viewmodel.ManageUsersViewModel

class EditUserFormFragment : Fragment(R.layout.fragment_edit_user_form) {

    private val viewModel: ManageUsersViewModel by activityViewModels()
    private var user: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = arguments?.getParcelable("USER")

        val etName = view.findViewById<EditText>(R.id.et_user_name)
        val etEmail = view.findViewById<EditText>(R.id.et_user_email)
        val etPassword = view.findViewById<EditText>(R.id.et_user_password)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinner_user_role)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        // Preenche os dados
        user?.let {
            etName.setText(it.name)
            etEmail.setText(it.email)
            val roles = arrayOf("COMMON", "ADMIN")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
            spinnerRole.adapter = adapter
            spinnerRole.setSelection(roles.indexOf(it.userType))
        }

        // Configura o clique do botão Salvar
        btnSave.setOnClickListener {
            user?.let {
                val newName = etName.text.toString()
                val newEmail = etEmail.text.toString()
                val newPassword = etPassword.text.toString()
                val newRole = spinnerRole.selectedItem.toString()

                viewModel.updateUser(it.id, newName, newEmail, if (newPassword.isNotBlank()) newPassword else null, newRole)
                Toast.makeText(context, "Usuário atualizado!", Toast.LENGTH_SHORT).show()
                activity?.finish() // Fecha a tela de edição
            }
        }
    }

    companion object {
        fun newInstance(user: User): EditUserFormFragment {
            val fragment = EditUserFormFragment()
            val args = Bundle()
            args.putParcelable("USER", user)
            fragment.arguments = args
            return fragment
        }
    }
}