/**
 * 
 */
package pl.edu.mimuw.crawler.kp385996;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Klasa zawierająca pomocnicze, statyczne metody do przetwarzania adresów i
 * ścieżek.
 * 
 * @author Krzysztof Piesiewicz
 */
public class LinkUtil {

	/**
	 * Zwraca adres/ścieżkę pliku na podstawie względnej ścieżki @relativePath
	 * oraz adresów/ścieżek @parent i @baseURI. (Zwracana ścieżka jest względem
	 * programu).
	 * 
	 * Jeśli adres/ścieżka rozpoczyna się od "//", to trakowany/-na
	 * jest jako adres/ścieżka bezwględny/-na.
	 * 
	 * Gdy zaczyna się tylko od "/", to trakowany/-a
	 * jest jako względny/-a adres/ścieżka od @baseURI.
	 * 
	 * Gdy nie zaczyna się od "/", to tratkowany/-a
	 * jest jako względny/-a adres/ścieżka od @parent.
	 * 
	 * @param parent
	 * @param relativePath
	 * @param baseURI
	 * @return
	 */
	public static String getAddressOrPath(String parent, String relativePath, String baseURI) {
		if (relativePath.startsWith("//")) {
			return getURLWithRepairedProtocol(relativePath);
		}
		if (relativePath.startsWith("/")) {
			return getAddressOrPathWithBaseURI(baseURI, relativePath);
		}
		return getPathWithParent(parent, relativePath);
	}

	/**
	 * Zwraca adres / prostą ściężkę pliku z podanego względnego adresu /
	 * względnej ścieżki do @baseURI. (Zwracana ścieżka jest względna do
	 * programu).
	 * 
	 * @param baseURI
	 * @param relativeAddressOrPath
	 * @return
	 */
	public static String getAddressOrPathWithBaseURI(String baseURI, String relativeAddressOrPath) {
		if (!relativeAddressOrPath.startsWith("/"))
			relativeAddressOrPath = "/" + relativeAddressOrPath;
		return getDecodedAddressOrPath(baseURI + relativeAddressOrPath);
	}

	/**
	 * Zwraca prostą, względną ściężkę dostępu do pliku (względem programu).
	 * (Taka ścieżka jest jednoznaczna).
	 * 
	 * @param parent
	 * @param relativePath
	 * @return
	 */
	public static String getPathWithParent(String parent, String relativePath) {
		String path = "";
		try {
			path = new File("").toURI()
					.relativize(new File(getCanonicalPath(parent, relativePath)).toURI()).getPath();
		} catch (Exception e) {
		}
		return path;
	}

	/**
	 * Zwraca Kanoniczną (w szczególności bezwgzlędną) ściężkę dostępu do pliku.
	 * 
	 * @param parent
	 * @param relativePath
	 * @return
	 */
	public static String getCanonicalPath(String parent, String relativePath) {
		relativePath = getDecodedAddressOrPath(relativePath);
		parent = getDecodedAddressOrPath(parent);
		try {
			return new File(parent, relativePath).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Zwraca adres/ścieżkę w kodowaniu UTF-8 zamiast z użyciem znaków %xx
	 * 
	 * @param addressOrPath
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getDecodedAddressOrPath(String addressOrPath) {
		return URLDecoder.decode(addressOrPath);
	}

	/**
	 * Czy podany string jest adresem internetowym?
	 * 
	 * @param addressOrPath
	 * @return
	 */
	public static boolean isInternetAddress(String addressOrPath) {
		return addressOrPath.startsWith("http://") || addressOrPath.startsWith("https://");
	}

	/**
	 * Sprawdza czy podany plik o podanym adresie/ścieżce może być stroną
	 * webową.
	 * 
	 * Gdy @addressOrPath jest ściężką dostępu do pliku bez rozszerzenia
	 * spośród ".html", ".htm", ".php", ".asp", ".jsp", ".aspx", to traktowany
	 * jest jako
	 * potencjalna strona.
	 * 
	 * Metoda zwraca false wyłącznie dla: ".pdf", ".txt", ".dvi", ".ps", ".cpp",
	 * ".c", ".py", ".java", ".jar", ".zip", ".gz", ".tar", ".doc", ".docx",
	 * ".odt", ".rtf", ".xls", ".tex", ".jpg", ".png", ".svg", ".gif"
	 * 
	 * @param addressOrPath
	 * @return
	 */
	public static boolean mightBeAHtmlFilePathOrAddress(String addressOrPath) {
		String[] exts = {".html", ".htm", ".php", ".asp", ".jsp", ".aspx"};
		String[] wrongExts = {".pdf", ".txt", ".dvi", ".ps", ".cpp", ".c", ".py", ".java", ".jar",
				".zip", ".gz", ".tar", ".doc", ".docx", ".odt", ".rtf", ".xls", ".tex", ".jpg",
				".png", ".svg", ".gif"};

		for (String ext : exts) {
			if (addressOrPath.endsWith(ext)) {
				return true;
			}
		}
		for (String wrongExt : wrongExts) {
			if (addressOrPath.endsWith(wrongExt)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Zwraca nazwę domeny bez predrostka protokołu sieciowego.
	 * 
	 * @param address
	 * @return
	 */
	public static String getDomainWithoutProtocolPrefix(String address) {
		address = address.replace("https://", "").replace("http://", "").replace("www.", "");
		try {
			address = address.substring(0, address.indexOf('/'));
		} catch (Exception e) {
			// @address nie zawiera znaku '/'
		}
		return address;
	}

	/**
	 * Zwraca adres z protokołem https.
	 * 
	 * @param address
	 * @return
	 */
	public static String getURLWithRepairedProtocol(String address) {
		address = address.replace("http://", "https://");
		if (!address.startsWith("https://")) {
			address = "https://" + address;
		}
		return address;
	}

	/**
	 * Zwraca adres bez „query strings”.
	 * 
	 * @param address
	 * @return
	 */
	public static String getURLWithoutQueries(String address) {
		char[] forbiddenChars = {'?', ';', '#', ','};

		for (char c : forbiddenChars) {
			int index = address.indexOf(c);
			if (index != -1)
				address = address.substring(0, address.indexOf(c));
		}
		return address;
	}

	/**
	 * Zwraca adres z protokołem https oraz bez „query strings”.
	 * 
	 * @param address
	 * @return
	 */
	public static String getSimpleRepairedURL(String address) {
		return getURLWithoutQueries(getURLWithRepairedProtocol(address));
	}
}
