package br.ufpe.cin.if710.podcast.ui;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;
import android.view.MenuItem;

public class EpisodeDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        ItemFeed itens = (ItemFeed) getIntent().getSerializableExtra("clickItem");

        TextView title_descricao = findViewById(R.id.title_descricao);
        title_descricao.setText(itens.getTitle());

        TextView link_descricao = findViewById(R.id.link_descricao);
        link_descricao.setText(itens.getLink());

        TextView pubDate_descricao = findViewById(R.id.pubDate_descricao);
        pubDate_descricao.setText(itens.getPubDate());

        TextView descricao_descricao = findViewById(R.id.descricao_descricao);
        descricao_descricao.setText(itens.getDescription());

        TextView donwload_link_descricao = findViewById(R.id.donwload_link_descricao);
        donwload_link_descricao.setText(itens.getDownloadLink());

    }
}
