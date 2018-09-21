/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

/**
 * Interfejs {@link}Crawler muszą implementować
 * wszystkie klasy crawlerów przekazanych do biblioteki.
 * 
 * @author Krzysztof Piesiewicz
 */
public interface Crawler {

	/**
	 * Metoda zostanie wywołana przez metodę {@link}startProccessing w obiekcie
	 * klasy {@link}DocumnetQueueManager. Jako parametr zostanie przekazany
	 * obiekt wywołujący metodę.
	 * 
	 * @param docManager
	 */
	public void init(DocumentQueueManager docManager);

	/**
	 * Metoda zostanie wywołana po zakończeniu przetwarzania wszystkich
	 * dokumentów przez obiekt klasy DocumentQueueManager.
	 * Jako parametr zostanie przekazany obiekt wywołujący metodę.
	 * 
	 * @param docManager
	 */
	public void terminate(DocumentQueueManager docManager);

	/**
	 * Przetwarza podaną jako argument stronę. Metoda zostanie wywołana dla
	 * każdego dokumentu przetwarzanego przez obiekt klasy DocumentQueueManager.
	 * Jako parametry zostaną przekazane: obiekt wywołujący metodę oraz dokument
	 * do przetworzenia.
	 * 
	 * @param docManager
	 * @param doc
	 */
	public void processPage(DocumentQueueManager docManager, DocumentLabel docLabel);
	
	/**
	 * Przetwarza podany dokument, który nie jest stroną lub nie udało się załadować go jako stronę,
	 * lub nawet nie udało się go pobrać.
	 * Jako parametry zostaną przekazane: obiekt wywołujący metodę oraz dokument
	 * do przetworzenia.
	 * 
	 * @param docManager
	 * @param docLabel
	 */
	public void processDocument(DocumentQueueManager docManager, DocumentLabel docLabel);
}
