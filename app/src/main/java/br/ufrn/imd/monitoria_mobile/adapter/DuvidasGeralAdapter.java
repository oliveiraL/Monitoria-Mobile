package br.ufrn.imd.monitoria_mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.imd.monitoria_mobile.R;
import br.ufrn.imd.monitoria_mobile.activity.AlunoDetalhesDuvidaActivity;
import br.ufrn.imd.monitoria_mobile.activity.ResponderDuvida;
import br.ufrn.imd.monitoria_mobile.helper.RoundedImageView;
import br.ufrn.imd.monitoria_mobile.model.Dados;
import br.ufrn.imd.monitoria_mobile.model.Duvida;

public class DuvidasGeralAdapter extends RecyclerView.Adapter<DuvidasGeralAdapter.DuvidasGeralViewHolder> {
    private List<Duvida> list;
    private Context context;

    public DuvidasGeralAdapter(List<Duvida> dataSet, Context context) {
        this.list = dataSet;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final DuvidasGeralViewHolder duvidaSimplesViewHolder, int i) {
        Drawable roundedImage = RoundedImageView.getRoundedImageView(list.get(i).getImagemUsuario(), 70, 70, 200.0f, this.context.getResources());
        duvidaSimplesViewHolder.vFotoUsuario.setImageDrawable(roundedImage);
        duvidaSimplesViewHolder.vNomeUsuario.setText(list.get(i).getNomeUsuario());
        duvidaSimplesViewHolder.vDisciplina.setText(list.get(i).getDisciplina());
        duvidaSimplesViewHolder.vDisciplina.setVisibility(View.VISIBLE);
        duvidaSimplesViewHolder.vData.setVisibility(View.GONE);

        if (list.get(i).getStatus() == Duvida.Status.FECHADA) {
            duvidaSimplesViewHolder.vStatus.setText("RESOLVIDA");
            duvidaSimplesViewHolder.vStatus.setVisibility(View.VISIBLE);
        } else {
            duvidaSimplesViewHolder.vStatus.setVisibility(View.INVISIBLE);
        }
        duvidaSimplesViewHolder.vTitulo.setText(list.get(i).getTitulo());
        duvidaSimplesViewHolder.vDescricao.setText(list.get(i).getDescricao());
        duvidaSimplesViewHolder.vCurtidas.setText(list.get(i).getTotalCurtidas() + " curtidas");
        duvidaSimplesViewHolder.vRespostas.setText(list.get(i).getRespostas().size() + " respostas");

        if (list.get(i).isCurtida()) {
            duvidaSimplesViewHolder.vBtnCurtir.setVisibility(View.GONE);
            duvidaSimplesViewHolder.vBtnDescurtir.setVisibility(View.VISIBLE);
        } else {
            duvidaSimplesViewHolder.vBtnCurtir.setVisibility(View.VISIBLE);
            duvidaSimplesViewHolder.vBtnDescurtir.setVisibility(View.GONE);
        }

        if (list.get(i).getFoto() != -1) {
            Bitmap mBitmap = BitmapFactory.decodeResource(this.context.getResources(), list.get(i).getFoto());
            duvidaSimplesViewHolder.vFoto.setImageBitmap(mBitmap);
            duvidaSimplesViewHolder.vOptionalFoto.setVisibility(View.VISIBLE);
        } else {
            duvidaSimplesViewHolder.vOptionalFoto.setVisibility(View.GONE);
        }

        final Duvida d = list.get(i);

        duvidaSimplesViewHolder.vBtnResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context.getApplicationContext(), ResponderDuvida.class);
                i.putExtra("duvida", d);
                context.startActivity(i);
            }
        });


        duvidaSimplesViewHolder.vCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context.getApplicationContext(), AlunoDetalhesDuvidaActivity.class);
                        i.putExtra("duvida", d);
                        context.startActivity(i);
                        // Snackbar.make(v, "Ver detalhes da dúvida não implementado ainda!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
        );

        duvidaSimplesViewHolder.vBtnCurtir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                d.setCurtida(true);
                curtir(d,0);
                notifyItemChanged(duvidaSimplesViewHolder.getAdapterPosition());
            }
        });

        duvidaSimplesViewHolder.vBtnDescurtir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                d.setCurtida(false);
                curtir(d,1);
                notifyItemChanged(duvidaSimplesViewHolder.getAdapterPosition());
            }
        });

    }

    @Override
    public DuvidasGeralViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardlayout_duvidasimples, viewGroup, false);

        return new DuvidasGeralViewHolder(itemView);
    }

    public static class DuvidasGeralViewHolder extends RecyclerView.ViewHolder {
        protected TextView vNomeUsuario;
        protected ImageView vFotoUsuario;
        protected TextView vDisciplina;
        protected TextView vData;
        protected TextView vStatus;
        protected TextView vTitulo;
        protected TextView vDescricao;
        protected TextView vCurtidas;
        protected TextView vRespostas;

        protected Button vBtnCurtir;
        protected Button vBtnDescurtir;
        protected Button vBtnResponder;

        protected CardView vCard;
        protected ImageView vFoto;
        protected RelativeLayout vOptionalFoto;


        public DuvidasGeralViewHolder(View v) {
            super(v);
            vNomeUsuario = (TextView) v.findViewById(R.id.duvidaSimples_nomeUsusario);
            vFotoUsuario = (ImageView) v.findViewById(R.id.duvidaSimples_fotoUsusario);
            vDisciplina = (TextView) v.findViewById(R.id.duvidaSimples_disciplina);
            vData = (TextView) v.findViewById(R.id.duvidaSimples_data);
            vStatus = (TextView) v.findViewById(R.id.duvidaSimples_status);
            vTitulo = (TextView) v.findViewById(R.id.duvidaSimples_titulo);
            vDescricao = (TextView) v.findViewById(R.id.duvidaSimples_descricao);
            vCurtidas = (TextView) v.findViewById(R.id.duvidaSimples_curtidas);
            vRespostas = (TextView) v.findViewById(R.id.duvidaSimples_respostas);

            vBtnCurtir = (Button) v.findViewById(R.id.duvidaSimples_btnCurtir);
            vBtnDescurtir = (Button) v.findViewById(R.id.duvidaSimples_btnDescurtir);
            vBtnResponder = (Button) v.findViewById(R.id.duvidaSimples_btnResponder);


            vCard = (CardView) v.findViewById(R.id.duvidaSimples_card);
            vOptionalFoto = (RelativeLayout) v.findViewById(R.id.duvidaSimples_opcionalImage);
            vFoto = (ImageView) v.findViewById(R.id.duvidaSimples_foto);

        }
    }

    public List<Duvida> getList() {
        return list;
    }

    public void setList(List<Duvida> list) {
        this.list = list;
    }

    private void curtir(final Duvida duvida, Integer curtir){


        Map<String,String> params = new HashMap<String, String>();
        params.put("idPessoa", Dados.getPerfil().getPessoa().getId()+"");
        params.put("idDuvida",duvida.getId()+"");
        params.put("idResposta", "0");
        params.put("curtir", curtir.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://172.20.10.4:8080/monitoria/api/curtida/post", new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }



        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.context.getApplicationContext());
        requestQueue.add(jsonObjReq);
    }
}
