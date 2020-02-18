package com.example.appcadastro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import java.util.Locale;

public class CadastrarActivity extends AppCompatActivity {


    /*COMPONENTES DA TELA*/
    EditText editTextNome;
    EditText editTextEndereco;
    RadioButton radioButtonMasculino;
    RadioButton radioButtonFeminino;
    RadioGroup radioGroupSexo;
    EditText editTextDataNascimento;
    Spinner spinnerEstadoCivil;
    CheckBox checkBoxRegistroAtivo;
    Button buttonSalvar;
    Button buttonVoltar;

    //CRIA POPUP COM O CALENDÁRIO
    DatePickerDialog datePickerDialogDataNascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        // CHAMA O METODO PARA DIZER QUAL A LOCALIZAÇÃO,
        // USADO PARA TRADUZIR OS TEXTOS DO CALENDÁRIO.
        this.Localizacao();

        //VINCULA OS COMPONENTES DA TELA COM OS DA ATIVIDADE
        this.CriarComponentes();

        //CRIA OS EVENTOS DOS COMPONENTES
        this.CriarEventos();

        //CARREGA AS OPÇÕES DE ESTADO CIVIL
        this.CarregaEstadosCivis();
    }

    //VINCULA OS COMPONENTES DA TELA COM OS DA ATIVIDADE
    protected void CriarComponentes() {

        editTextNome = (EditText) this.findViewById(R.id.editTextNome);

        editTextEndereco = (EditText) this.findViewById(R.id.editTextEndereco);

        radioButtonMasculino = (RadioButton) this.findViewById(R.id.radioButtonMasculino);

        radioButtonFeminino = (RadioButton) this.findViewById(R.id.radioButtonFeminino);

        radioGroupSexo = (RadioGroup) this.findViewById(R.id.radioGroupSexo);

        editTextDataNascimento = (EditText) this.findViewById(R.id.editTextDataNascimento);

        spinnerEstadoCivil = (Spinner) this.findViewById(R.id.spinnerEstadoCivil);

        checkBoxRegistroAtivo = (CheckBox) this.findViewById(R.id.checkBoxRegistroAtivo);

        buttonSalvar = (Button) this.findViewById(R.id.buttonSalvar);

        buttonVoltar = (Button) this.findViewById(R.id.buttonVoltar);

    }

    //CRIA OS EVENTOS DOS COMPONENTES
    protected void CriarEventos() {


        final Calendar calendarDataAtual = Calendar.getInstance();
        int anoAtual = calendarDataAtual.get(Calendar.YEAR);
        int mesAtual = calendarDataAtual.get(Calendar.MONTH);
        int diaAtual = calendarDataAtual.get(Calendar.DAY_OF_MONTH);

        //MONTANDO O OBJETO DE DATA PARA PREENCHER O CAMPOS QUANDO  FOR SELECIONADO
        //FORMATO DD/MM/YYYY
        datePickerDialogDataNascimento = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int anoSelecionado, int mesSelecionado, int diaSelecionado) {

                //FORMATANDO O MÊS COM DOIS DÍGITOS
                String mes = (String.valueOf((mesSelecionado + 1)).length() == 1 ? "0" + (mesSelecionado + 1) : String.valueOf(mesSelecionado));

                editTextDataNascimento.setText(diaSelecionado + "/" + mes + "/" + anoSelecionado);

            }

        }, anoAtual, mesAtual, diaAtual);


        //CRIANDO EVENTO NO CAMPO DE DATA PARA ABRIR A POPUP
        editTextDataNascimento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                datePickerDialogDataNascimento.show();
            }
        });


        //CRIANDO EVENTO NO CAMPO DE DATA PARA ABRIR A POPUP
        editTextDataNascimento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                datePickerDialogDataNascimento.show();

            }
        });

        //CRIANDO EVENTO NO BOTÃO SALVAR
        buttonSalvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Salvar_onClick();
            }
        });

        //CRIANDO EVENTO NO BOTÃO VOLTAR
        buttonVoltar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentMainActivity);
                finish();
            }
        });
    }

    //VALIDA OS CAMPOS E SALVA AS INFORMAÇÕES NO BANCO DE DADOS
    protected void Salvar_onClick() {

        if (editTextNome.getText().toString().trim().equals("")) {

            Uteis.Alert(this, this.getString(R.string.nome_obrigatorio));

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

            /*SETANDO O VALOR DO CAMPO NOME*/
            pessoaModel.setNome(editTextNome.getText().toString().trim());

            /*SETANDO O ENDEREÇO*/
            pessoaModel.setEndereco(editTextEndereco.getText().toString().trim());

            /*SETANDO O SEXO*/
            if (radioButtonMasculino.isChecked()) {
                pessoaModel.setSexo("M");
            } else
                pessoaModel.setSexo("F");

            /*SETANDO A DATA DE NASCIMENTO*/
            pessoaModel.setDataNascimento(editTextDataNascimento.getText().toString().trim());

            /*REALIZANDO UM CAST PARA PEGAR O OBJETO DO ESTADO CIVIL SELECIONADO*/
            EstadoCivilModel estadoCivilModel = (EstadoCivilModel) spinnerEstadoCivil.getSelectedItem();

            /*SETANDO ESTADO CIVIL*/
            pessoaModel.setEstadoCivil(estadoCivilModel.getCodigo());


            /*SETA O REGISTRO COMO INATIVO*/
            pessoaModel.setRegistroAtivo((byte) 0);

            /*SE TIVER SELECIONADO SETA COMO ATIVO*/
            if (checkBoxRegistroAtivo.isChecked())
                pessoaModel.setRegistroAtivo((byte) 1);

            /*SALVANDO UM NOVO REGISTRO*/
            new PessoaRepository(this).Salvar(pessoaModel);

            /*MENSAGEM DE SUCESSO!*/
            Uteis.Alert(this, this.getString(R.string.registro_salvo_sucesso));

            LimparCampos();
        }


    }

    //LIMPA OS CAMPOS APÓS SALVAR AS INFORMAÇÕES
    protected void LimparCampos() {

        editTextNome.setText(null);
        editTextEndereco.setText(null);

        radioGroupSexo.clearCheck();

        editTextDataNascimento.setText(null);
        checkBoxRegistroAtivo.setChecked(false);
    }

    //DIZ QUAL A LOCALIZAÇÃO PARA TRADUZIR OS TEXTOS DO CALENDÁRIO.
    protected void Localizacao() {

        Locale locale = new Locale("pt", "BR");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);

    }

    //CARREGA AS OPÇÕES DE ESTADO CIVIL PARA O COMPONENTE SPINNER
    protected void CarregaEstadosCivis() {

        ArrayAdapter<EstadoCivilModel> arrayAdapter;

        List<EstadoCivilModel> itens = new ArrayList<EstadoCivilModel>();

        itens.add(new EstadoCivilModel("S", "Solteiro(a)"));
        itens.add(new EstadoCivilModel("C", "Casado(a)"));
        itens.add(new EstadoCivilModel("V", "Viuvo(a)"));
        itens.add(new EstadoCivilModel("D", "Divorciado(a)"));


        arrayAdapter = new ArrayAdapter<EstadoCivilModel>(this, android.R.layout.simple_spinner_item, itens);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerEstadoCivil.setAdapter(arrayAdapter);

    }
}
