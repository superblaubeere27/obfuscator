package me.superblaubeere27.jobf.utils;

import com.google.common.io.ByteStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Templates {
    private static List<Template> TEMPLATES = new ArrayList<>();

    public static void loadTemplates() {
        try {
            for (String name : getResourceFiles("/templates")) {
                TEMPLATES.add(new Template(name.replace(".json", ""), new String(ByteStreams.toByteArray(Templates.class.getResourceAsStream("/templates/" + name)), "UTF-8")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? Templates.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static List<Template> getTemplates() {
        return TEMPLATES;
    }
}
