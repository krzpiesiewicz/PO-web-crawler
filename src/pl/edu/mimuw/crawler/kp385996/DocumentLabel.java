/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * DocumentLabel jest znacznikiem dokumentu takiego jak: strona html,
 * dowolna jej część lub jakikolwiek inny dokument.
 * 
 * DocumentLabel posiada adres/ścieżkę dokumentu (adres www, lokalną ścieżkę
 * do dokumentu). W szczególności adres może być niezdefiniowany.
 * 
 * DocumentLabel posiada referencję do org.jsoup.nodes.Document
 * 
 * @author Krzysztof Piesiewicz
 */
public class DocumentLabel {
	private String address, diskPath, parent, baseURI;
	private Object information;
	private Document jdoc;
	private Integer hashCode;

	private DocumentLabel(Object information) {
		this.information = information;
	}

	public DocumentLabel(String address, Object information) {
		this(information);
		this.address = address;
		this.hashCode = hashCode();
	}

	public DocumentLabel(String parent, String relativePath, String baseURI, Object information) {
		this(information);
		this.baseURI = baseURI;
		diskPath = LinkUtil.getAddressOrPath(parent, relativePath, baseURI);
		if (parent != null) {
			parent = new File(diskPath).getParent();
		}
		this.parent = parent;
		hashCode = hashCode();
	}

	/**
	 * Tworzy nowy obiekt klasy Dokument dla obietku klasy
	 * org.jsoup.nodes.Document. Uwaga. W przypadku użycia tego konstruktora nie
	 * są znane: adres ani ścieżka dokumentu.
	 * 
	 * @param jdoc
	 */
	public DocumentLabel(org.jsoup.nodes.Document jdoc) {
		this.jdoc = jdoc;
	}

	/**
	 * Ładuje źródło dokumentu.
	 */
	public int loadSource() {
		if (jdoc == null) {
			if (address != null) {
				try {
					jdoc = Jsoup.connect(address).get();
				} catch (Exception e) {
					return -1;
				}
				if (jdoc != null) {
					baseURI = jdoc.baseUri();
				}
			} else {
				File input = new File(diskPath);
				try {
					jdoc = Jsoup.parse(input, null, baseURI);
				} catch (IOException e) {
					return -1;
				}
			}
		}
		return 0;
	}

	/**
	 * Zwalnie referencje do wszystkiego, co nie jest potrzebne do porównywania
	 * etykiet dokumentów.
	 */
	public void releaseSource() {
		jdoc = null;
		parent = null;
		baseURI = null;
		information = null;
	}

	public Elements getElementsByAttribute(String key) {
		return jdoc.select(key);
	}

	/**
	 * Zwraca bezwględne linki z dokumentu jako listę stringów.
	 * 
	 * @return
	 */
	public ArrayList<String> getAbsoluteLinks() {

		ArrayList<String> arrList = new ArrayList<>();
		Elements elements = getElementsByAttribute("a[href]");

		for (Element e : elements) {
			String link = LinkUtil.getSimpleRepairedURL(e.attr("abs:href"));
			if (link.isEmpty()) {
				if (LinkUtil.isInternetAddress(address)) {
					link = LinkUtil.getAddressOrPath(parent, e.attr("href"), baseURI);
					link = LinkUtil.getSimpleRepairedURL(link);
				} else {
					link = LinkUtil.getCanonicalPath("",
							LinkUtil.getAddressOrPath(parent, e.attr("href"), baseURI));
				}
			}
			if (!link.isEmpty())
				arrList.add(link);
		}

		return arrList;
	}

	/**
	 * Zwraca orginalnie zapisane linki z dokumentu jako listę stringów.
	 * 
	 * @return
	 */
	public ArrayList<String> getLinks() {

		ArrayList<String> arrList = new ArrayList<>();
		Elements elements = getElementsByAttribute("a[href]");

		for (Element e : elements) {
			arrList.add(e.attr("href"));
		}

		return arrList;
	}

	public String getAddress() {
		return address;
	}

	public String getParent() {
		return parent;
	}

	public String getPath() {
		return diskPath;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public Document getJsoupDocument() {
		return jdoc;
	}

	public Object getInformation() {
		return information;
	}

	@Override
	public int hashCode() {

		if (hashCode == null) {
			String s = "";
			if (address != null) {
				s += address;
			}
			if (diskPath != null) {
				s += ("@" + diskPath);
			}
			hashCode = s.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DocumentLabel) {
			if (address != null) {
				return address.equals(((DocumentLabel) o).getAddress());
			} else if (diskPath != null) {
				return diskPath.equals(((DocumentLabel) o).getPath());
			} else {
				return ((DocumentLabel) o).getAddress() == null
						&& ((DocumentLabel) o).getPath() == null;
			}
		}
		return false;
	}
}
