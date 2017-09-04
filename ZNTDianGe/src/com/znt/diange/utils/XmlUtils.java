
package com.znt.diange.utils; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.znt.diange.mina.entity.MediaInfor;


/** 
 * @ClassName: MyXmlUtil 
 * @Description: xml文件处理类  依赖 dom4j-2.0.0-ALPHA-2.jar 和 jaxen-1.1-beta-6.jar
 * 必须要导入jaxen-1.1-beta-6.jar，因为dom4j依赖这个包，
      否则在选择节点：List<Element> idList = (List<Element>) doc.selectNodes("//root");
      会报:Could not find class 'org.jaxen.dom4j.Dom4jXPath'
   java.lang.VerifyError: org/dom4j/xpath/DefaultXPath错误
 * @author yan.yu 
 * @date 2014-2-26 下午5:19:25  
 */
public class XmlUtils
{

	/** Dom方式，创建 XML  */
	public static String domCreateXML(MediaInfor music) 
	{
		String xmlWriter = null;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element eleRoot = doc.createElement("DIDL-Lite");
			eleRoot.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
			eleRoot.setAttribute("xmlns:upnp", "urn:schemas-upnp-org:metadata-1-0/upnp/");
			eleRoot.setAttribute("xmlns", "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/");
			eleRoot.setAttribute("xmlns:av", "urn:schemas-sony-com:av");
			eleRoot.setAttribute("xmlns:dlna", "urn:schemas-dlna-org:metadata-1-0/");
			eleRoot.setAttribute("xmlns:sec", "http://www.sec.co.kr/");
			eleRoot.setAttribute("xmlns:pv", "http://www.pv.com/pvns/");
			eleRoot.setAttribute("xmlns:pv", "http://www.pv.com/pvns/");
			doc.appendChild(eleRoot);
			
			Element eleMusic = doc.createElement("item");
			eleMusic.setAttribute("id", "-1");
			eleMusic.setAttribute("parentID", "-1");
			eleRoot.appendChild(eleMusic);
			
			
			Element eleClass = doc.createElement("upnp:class");
			Node nodeClass = doc.createTextNode("object.item.audioItem.musicTrack");
			eleClass.appendChild(nodeClass);
			eleMusic.appendChild(eleClass);
			
			Element eleTitle = doc.createElement("dc:title");
			CDATASection cdataTitle = doc.createCDATASection(music.getMediaName());
			//Node nodeTile = doc.createTextNode( );
			eleTitle.appendChild(cdataTitle);
			eleMusic.appendChild(eleTitle);
			
			Element eleAlbum = doc.createElement("upnp:album");
			Node nodeAlbum = doc.createTextNode( music.getAlbumName());
			eleAlbum.appendChild(nodeAlbum);
			eleMusic.appendChild(eleAlbum);
			
			Element eleArtist = doc.createElement("upnp:artist");
			CDATASection cdataArtist = doc.createCDATASection(music.getArtist());
			//Node nodeArtist = doc.createTextNode( music.getArtist());
			eleArtist.appendChild(cdataArtist);
			eleMusic.appendChild(eleArtist);
			
			Element eleAlbumArtURI = doc.createElement("upnp:albumArtURI");
			Node nodeAlbumArtURI = doc.createTextNode( music.getAlbumUrl());
			eleAlbumArtURI.appendChild(nodeAlbumArtURI);
			eleMusic.appendChild(eleAlbumArtURI);
			
			/*Element eleLyric = doc.createElement("upnp:lyric");
			Node nodeLyric = doc.createTextNode( music.getLyric());
			eleLyric.appendChild(nodeLyric);
			eleMusic.appendChild(eleLyric);*/
			
			Element eleId = doc.createElement("upnp:id");
			Node nodeId = doc.createTextNode( music.getMediaId());
			eleId.appendChild(nodeId);
			eleMusic.appendChild(eleId);
			
			Element eleType = doc.createElement("upnp:type");
			Node nodeType = doc.createTextNode( music.getMediaType());
			eleType.appendChild(nodeType);
			eleMusic.appendChild(eleType);
			
			Element eleRes = doc.createElement("res");
			eleRes.setAttribute("size", music.getMediaSize() + "");
			eleRes.setAttribute("duration", music.getMediaDuration()+ "");
			eleRes.setAttribute("protocolInfo", "http-get:*:audio/mpeg:*");
			Node nodeRes = doc.createTextNode(music.getMediaUrl());
			eleRes.appendChild(nodeRes);
			eleMusic.appendChild(eleRes);

			Properties properties = new Properties();
			properties.setProperty(OutputKeys.INDENT, "yes");
			properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
			properties.setProperty(OutputKeys.VERSION, "1.0");
			properties.setProperty(OutputKeys.ENCODING, "utf-8");
			properties.setProperty(OutputKeys.METHOD, "xml");
			properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperties(properties);
			
			DOMSource domSource = new DOMSource(doc.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			
			xmlWriter = output.toString();
			
		} 
		catch (ParserConfigurationException e) 
		{		
			// factory.newDocumentBuilder
			e.printStackTrace();
		} 
		catch (DOMException e) 
		{						
			// doc.createElement
			e.printStackTrace();
		} 
		catch (TransformerFactoryConfigurationError e) 
		{		
			// TransformerFactory.newInstance
			e.printStackTrace();
		} 
		catch (TransformerConfigurationException e) 
		{		
			// transformerFactory.newTransformer
			e.printStackTrace();
		} 
		catch (TransformerException e) 
		{				
			// transformer.transform
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if(xmlWriter != null)
			return xmlWriter.toString();
		else
			return "";
		//createFromMetaData(xmlStr);
		
	}
	
	public static void createFromMetaData(String metadata)
	{
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		
		if (metadata.contains("&") && !metadata.contains("&amp;"))
		{
			metadata = metadata.replace("&", "&amp;");
		}

		try 
		{
			documentBuilder = dfactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(metadata.getBytes("UTF-8"));
			Document doc = documentBuilder.parse(is);
			String ObjectClass = (getElementValue(doc,"upnp:class"));
			String Title = (getElementValue(doc,"dc:title"));
			String Album = (getElementValue(doc,"upnp:album"));
			String Artist = (getElementValue(doc,"upnp:artist"));			
			String AlbumUri = (getElementValue(doc,"upnp:albumArtURI"));
			String Lyrc = (getElementValue(doc,"upnp:lyric"));
			String Type = (getElementValue(doc,"upnp:type"));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static String getElementValue(Document doc , String element)
	{
		NodeList containers = doc.getElementsByTagName(element);
		for (int j = 0; j < containers.getLength(); ++j) 
		{		
			Node container = containers.item(j);
			NodeList childNodes = container.getChildNodes();
			if(childNodes.getLength()!=0)
			{
				Node childNode = childNodes.item(0);
				return childNode.getNodeValue();
			}
		}
		return "";
	}
}
 
