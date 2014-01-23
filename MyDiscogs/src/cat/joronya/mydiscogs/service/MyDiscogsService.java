package cat.joronya.mydiscogs.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import cat.joronya.mydiscogs.MyDiscogs;
import cat.joronya.mydiscogs.data.Collection;
import cat.joronya.mydiscogs.data.CollectionWorker;
import cat.joronya.mydiscogs.data.FieldWorker;
import cat.joronya.mydiscogs.data.FolderWorker;
import cat.joronya.mydiscogs.data.Profile;
import cat.joronya.mydiscogs.data.ProfileWorker;

/**
 * Rep Intents del MyDiscogsServiceHelper i els va encuant
 * i executant ordenadament en un thread apart per no
 * bloquejar aqui.
 * 
 * @author pol
 */
public class MyDiscogsService extends IntentService
{
	public MyDiscogsService()
	{
		super("MyDiscogsService");
	}
	
	/**
	 * Respon a les diferents demandes de les Activities, el IntentService
	 * ja s'encarrega de la cua i de passar-nos intents un a un. Tambe fa
	 * que s'executi en un worker thread.
	 */
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		// mirem que ens demanen executar
		Uri uri = intent.getData();
		
		// sync profile data
		if( Profile.CONTENT_URI.equals(uri) && Intent.ACTION_GET_CONTENT.equals(intent.getAction()) )
		{
			String username = intent.getStringExtra(MyDiscogs.USERNAME_EXTRA);
			ProfileWorker worker = new ProfileWorker();
			worker.sync(this, username);
		}
		
		// sync collection data, begin on folders and so on...
		if( Collection.CONTENT_URI.equals(uri) && Intent.ACTION_GET_CONTENT.equals(intent.getAction()) )
		{
			// sync folders
			String username = intent.getStringExtra(MyDiscogs.USERNAME_EXTRA);
			FolderWorker wFolder = new FolderWorker();
			wFolder.sync(this, username);
			
			// sync fields
			FieldWorker wField = new FieldWorker();
			wField.sync(this, username);
			
			// sync collection
			CollectionWorker wCollection = new CollectionWorker();
			wCollection.sync(this, username);
		}
	}
	
}