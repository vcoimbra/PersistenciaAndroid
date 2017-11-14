package br.com.fiap.persistencia;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import br.com.fiap.persistencia.dao.BaseDados;
import br.com.fiap.persistencia.model.Tarefa;
import br.com.fiap.persistencia.view.TarefaDialog;
import br.com.fiap.persistencia.view.adapter.TarefaAdapter;
import br.com.fiap.persistencia.view.listener.OnItemClickListener;
import br.com.fiap.persistencia.viewmodel.TarefaModel;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView rvTarefas;
    private TarefaAdapter adapter;
    private List<Tarefa> tarefas;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        rvTarefas = (RecyclerView) findViewById(R.id.rvTarefas);

        tarefas = new ArrayList<>();

        ViewModelProviders.of(this)
                .get(TarefaModel.class)
                .getTarefas()
                .observe(this, new Observer<List<Tarefa>>() {
                    @Override
                    public void onChanged(@Nullable List<Tarefa> tarefas) {
                        adapter.setList(tarefas);
                        rvTarefas.getAdapter().notifyDataSetChanged();
                    }
                });

        rvTarefas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TarefaAdapter(tarefas, deleteClick);
        rvTarefas.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TarefaDialog dialog = new TarefaDialog();
                dialog.show(getFragmentManager(), "CriarTarefa");
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private OnItemClickListener deleteClick = new OnItemClickListener() {
        @Override
        public void onClick(int position) {
            BaseDados db = BaseDados.getDatabase(MainActivity.this.getApplicationContext());
            new ApagarAsyncTask(db).execute(adapter.getTarefa(position));
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class ApagarAsyncTask extends AsyncTask<Tarefa, Void, Void> {

        private BaseDados db;

        ApagarAsyncTask(BaseDados appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Tarefa... params) {
            db.tarefaDao().apagar(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Registro excluído com sucesso", Toast.LENGTH_SHORT).show();
        }
    }
}

