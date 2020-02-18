package com.example.appcadastro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcadastro.Uteis.Uteis;
import com.example.appcadastro.model.EstadoCivilModel;
import com.example.appcadastro.model.PessoaModel;
import com.example.appcadastro.repository.PessoaRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditarActivity extends AppCompatActivity {


    /*COMPONENTES DA TELA*/
    EditText editTextCodigo;
    EditText editTextNome;
    EditText editTextEndereco;
    RadioButton radioButtonMasculino;
    RadioButton radioButtonFeminino;
    RadioGroup radioGroupSexo;
    EditText editTextDataNascimento;
    Spinner spinnerEstadoCivil;
    CheckBox checkBoxRegistroAtivo;
    Button buttonAlterar;
    Button buttonVoltar;

    //CRIA POPUP COM O CALENDÁRIO
    DatePickerDialog datePickerDialogDataNascimento;

    ArrayAdapter<EstadoCivilModel> arrayAdapterEstadosCivis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);


        //CHAMA O MÉTODO PARA CRIAR OS COMPONENTES DA TELA
        this.CriarComponentes();

        //CHAMA O MÉTODO QUE CRIA EVENTOS PARA OS COMPONENTES
        this.CriarEventos();

        //CARREGA AS OPÇÕES DE ESTADO CIVIL
        this.CarregaEstadosCivis();

        //CARREGA OS VALORES NOS CAMPOS DA TELA.
        this.CarregaValoresCampos();
    }

    //VINCULA OS COMPONENTES DA TELA(VIEW) AOS OBJETOS DECLARADOS.
    protected void CriarComponentes() {

        editTextCodigo = (EditText) this.findViewById(R.id.editTextCodigo);

        editTextNome = (EditText) this.findViewById(R.id.editTextNome);

        editTextEndereco = (EditText) this.findViewById(R.id.editTextEndereco);

        radioButtonMasculino = (RadioButton) this.findViewById(R.id.radioButtonMasculino);

        radioButtonFeminino = (RadioButton) this.findViewById(R.id.radioButtonFeminino);

        radioGroupSexo = (RadioGroup) this.findViewById(R.id.radioGroupSexo);

        editTextDataNascimento = (EditText) this.findViewById(R.id.editTextDataNascimento);

        spinnerEstadoCivil = (Spinner) this.findViewById(R.id.spinnerEstadoCivil);

        checkBoxRegistroAtivo = (CheckBox) this.findViewById(R.id.checkBoxRegistroAtivo);

        buttonAlterar = (Button) this.findViewById(R.id.buttonAlterar);

        buttonVoltar = (Button) this.findViewById(R.id.buttonVoltar);

    }

    //MÉTODO CRIA OS EVENTOS PARA OS COMPONENTES
    protected void CriarEventos() {


        final Calendar calendarDataAtual = Calendar.getInstance();
        int anoAtual = calendarDataAtual.get(Calendar.YEAR);
        int mesAtual = calendarDataAtual.get(Calendar.MONTH);
        int diaAtual = calendarDataAtual.get(Calendar.DAY_OF_MONTH);

        //CRIANDO A POPUP COM O CALENDÁRIO
        datePickerDialogDataNascimento = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {

                //FORMATANDO O MÊS COM DOIS DÍGITOS
                String mes = (String.valueOf((mesSelecionado + 1)).length() == 1 ? "0" + (mesSelecionado + 1) : String.valueOf(mesSelecionado));

                editTextDataNascimento.setText(diaSelecionado + "/" + mes + "/" + anoSelecionado);

            }

        }, anoAtual, mesAtual, diaAtual);


        //CRIANDO EVENTO CLICK PARA O CAMPO DATA DE NASCIMENTO MOSTRAR A POPUP
        editTextDataNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                datePickerDialogDataNascimento.show();
            }
        });

        //CRIANDO EVENTO FOCUS PARA O CAMPO DATA DE NASCIMENTO MOSTRAR A POPUP
        editTextDataNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                datePickerDialogDataNascimento.show();

            }
        });

        //CRIANDO EVENTO CLICK PARA O BOTÃO ALTERAR
        buttonAlterar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Alterar_onClick();
            }
        });

        //CRIANDO EVENTO CLICK PARA O BOTÃO VOLTAR
        buttonVoltar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });
    }

    //ALTERA UM REGISTRO
    protected void Alterar_onClick() {

        //VALIDA SE OS CAMPOS ESTÃO VAZIOS ANTES DE ALTERAR O REGISTRO
        if (editTextNome.getText().toString().trim().equals("")) {

            Uteis.Alert(this, this.getString(R.string.nome_obrigatorio));

            //FOCO NO CAMPO
            editTextNome.requestFocus();
        } else if (editTextEndereco.getText().toString().trim().equals("")) {

            Uteis.Alert(this, this.getString(R.string.endereco_obrigatorio));

            editTextEndereco.requestFocus();

        } else if (!radioButtonMasculino.isChecked() && !radioButtonFeminino.isChecked()) {

            Uteis.Alert(this, this.getString(R.string.sexo_obrigatorio));
        } else if (editTextDataNascimento.getText().toString().trim().equals("")) {

            Uteis.Alert(this, this.getString(R.string.data_nascimento_obrigatorio));

            editTextDataNascimento.requestFocus();

        } else {


            /*CRIANDO UM OBJETO PESSOA*/
            PessoaModel pessoaModel = new PessoaModel();

            pessoaModel.setCodigo(Integer.parseInt(editTextCodigo.getText().toString()));

            /*SETANDO O VALOR DO CAMPO NOME*/
            pessoaModel.setNome(editTextNome.getText().toString().trim());

            /*SETANDO O ENDEREÇO*/
            pessoaModel.setEndereco(editTextEndereco.getText().toString().trim());

            /*SETANDO O SEXO*/
            if (radioButtonMasculino.isChecked())
                pessoaModel.setSexo("M");
            else
                pessoaModel.setSexo("F");

            /*SETANDO A DATA DE NASCIMENTO*/
            pessoaModel.setDataNascimento(editTextDataNascimento.getText().toString().trim());

            /*REALIZANDO UM CAST PARA PEGAR O OBJETO DO ESTADO CIVIL SELECIONADO*/
            EstadoCivilModel estadoCivilModel = (EstadoCivilModel) spinnerEstadoCivil.getSelectedItem();

            /*SETANDO ESTO CIVIL*/
            pessoaModel.setEstadoCivil(estadoCivilModel.getCodigo());


            /*SETA O REGISTRO COMO INATIVO*/
            pessoaModel.setRegistroAtivo((byte) 0);

            /*SE TIVER SELECIONADO SETA COMO ATIVO*/
            if (checkBoxRegistroAtivo.isChecked())
                pessoaModel.setRegistroAtivo((byte) 1);

            /*ALTERANDO O REGISTRO*/
            new PessoaRepository(this).Atualizar(pessoaModel);

            /*MENSAGEM DE SUCESSO!*/

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            //ADICIONANDO UM TITULO A NOSSA MENSAGEM DE ALERTA
            alertDialog.setTitle(R.string.app_name);

            //MENSAGEM A SER EXIBIDA
            alertDialog.setMessage("Registro alterado com sucesso! ");

            //CRIA UM BOTÃO COM O TEXTO OK SEM AÇÃO
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    //RETORNA PARA A TELA DE CONSULTA
                    Intent intentRedirecionar = new Intent(getApplicationContext(), ConsultarActivity.class);

                    startActivity(intentRedirecionar);

                    finish();
                }
            });

            //MOSTRA A MENSAGEM NA TELA
            alertDialog.show();


        }


    }

    //CARREGA OS ESTADOS CIVIS DO CAMPO spinnerEstadoCivil
    protected void CarregaEstadosCivis() {

        List<EstadoCivilModel> itens = new ArrayList<EstadoCivilModel>();

        itens.add(new EstadoCivilModel("S", "Solteiro(a)"));
        itens.add(new EstadoCivilModel("C", "Casado(a)"));
        itens.add(new EstadoCivilModel("V", "Viuvo(a)"));
        itens.add(new EstadoCivilModel("D", "Divorciado(a)"));


        arrayAdapterEstadosCivis = new ArrayAdapter<EstadoCivilModel>(this, android.R.layout.simple_spinner_item, itens);
        arrayAdapterEstadosCivis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerEstadoCivil.setAdapter(arrayAdapterEstadosCivis);

    }

    //POSICIONA O ESTADO CIVIL PARA EDIÇÃO
    protected void PosicionaEstadoCivil(String chaveEstadoCivil) {

        for (int index = 0; index < arrayAdapterEstadosCivis.getCount(); index++) {

            if (((EstadoCivilModel) arrayAdapterEstadosCivis.getItem(index)).getCodigo().equals(chaveEstadoCivil)) {

                spinnerEstadoCivil.setSelection(index);
                break;
            }

        }


    }

    //CARREGA OS VALORES NOS CAMPOS APÓS RETORNAR DO SQLITE
    protected void CarregaValoresCampos() {

        PessoaRepository pessoaRepository = new PessoaRepository(this);


        //PEGA O ID PESSOA QUE FOI PASSADO COMO PARAMETRO ENTRE AS TELAS
        Bundle extra = this.getIntent().getExtras();
        int id_pessoa = extra.getInt("id_pessoa");

        //CONSULTA UMA PESSOA POR ID
        PessoaModel pessoaModel = pessoaRepository.GetPessoa(id_pessoa);

        //SETA O CÓDIGO NA VIEW
        editTextCodigo.setText(String.valueOf(pessoaModel.getCodigo()));

        //SETA O NOME NA VIEW
        editTextNome.setText(pessoaModel.getNome());

        //SETA O ENDEREÇO NA VIEW
        editTextEndereco.setText(pessoaModel.getEndereco());

        //SETA O SEXO NA VIEW
        if (pessoaModel.getSexo().equals("M"))
            radioButtonMasculino.setChecked(true);
        else
            radioButtonFeminino.setChecked(true);

        //SETA A DATA DE NASCIMENTO
        editTextDataNascimento.setText(pessoaModel.getDataNascimento());

        //POSICIONA O ESTADO CIVIL
        this.PosicionaEstadoCivil(pessoaModel.getEstadoCivil());

        //SETA SE O  REGISTRO ESTÁ ATIVO
        if (pessoaModel.getRegistroAtivo() == 1)
            checkBoxRegistroAtivo.setChecked(true);
    }
}
