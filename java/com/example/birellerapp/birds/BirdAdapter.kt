import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.birellerapp.R
import com.bumptech.glide.Glide
import com.example.birellerapp.birds.BirdDataClass

class BirdAdapter(private val context: Context, private val birdList: List<BirdDataClass>) : BaseAdapter() {
    override fun getCount(): Int = birdList.size
    override fun getItem(position: Int): Any = birdList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.bird_item_view, parent, false)
        val bird = birdList[position]

        // Load the image using Glide
        val birdImageView = view.findViewById<ImageView>(R.id.birdList)
        Glide.with(context)
            .load(bird.imageUrl)
            .into(birdImageView)

        view.findViewById<TextView>(R.id.tvName).text = bird.name
       // view.findViewById<TextView>(R.id.tvLocation).text = bird.location

        return view
    }
}
