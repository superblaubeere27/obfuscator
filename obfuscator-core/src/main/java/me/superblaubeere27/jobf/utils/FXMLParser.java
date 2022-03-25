package me.superblaubeere27.jobf.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FXMLParser {

    public static String getControllerClassName(InputStream inputStream) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            Node rootNode = doc.getDocumentElement();
            Node controllerAttribute = rootNode.getAttributes().getNamedItem("fx:controller");
            if (controllerAttribute != null) {
                return controllerAttribute.getNodeValue();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] updateFXML(InputStream inputStream, FXMLControllerData controllerData) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            if (controllerData.getObfuscatedClassName() != null) {
                doc.getDocumentElement().setAttribute("fx:controller", controllerData.getObfuscatedClassName());
            }

            XPath xPath = XPathFactory.newInstance().newXPath();

            String methodExpression = "//@*[contains(., '#')]";
            NodeList methodNodeList = (NodeList) xPath.compile(methodExpression).evaluate(doc, XPathConstants.NODESET);

            for (int index=0; index < methodNodeList.getLength(); index++) {
                Node methodNode = methodNodeList.item(index);
                if (controllerData.getMethodsData().containsKey(methodNode.getNodeValue().substring(1))) {
                    methodNode.setNodeValue("#" + controllerData.getMethodsData().get(methodNode.getNodeValue().substring(1)));
                }
            }

            String fieldExpression = "//@*[name() = 'fx:id']";
            NodeList fieldsNodeList = (NodeList) xPath.compile(fieldExpression).evaluate(doc, XPathConstants.NODESET);

            for (int index=0; index < fieldsNodeList.getLength(); index++) {
                Node fieldNode = fieldsNodeList.item(index);
                String a = controllerData.getFieldsData().get(fieldNode.getNodeValue());
                if (controllerData.getFieldsData().containsKey(fieldNode.getNodeValue())) {
                    fieldNode.setNodeValue(controllerData.getFieldsData().get(fieldNode.getNodeValue()));
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            Source source = new DOMSource(doc);

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            StreamResult result=new StreamResult(byteArrayOutputStream);
            transformer.transform(source, result);
            return byteArrayOutputStream.toByteArray();
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException |
                TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }

}
