package ru.tpu.hostel.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    @Override
    public String getHeader(String name) {
        // Возвращаем заголовок из customHeaders, если он был добавлен
        String value = customHeaders.get(name);
        return (value != null) ? value : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Собираем все стандартные заголовки и добавленные
        Enumeration<String> headerNames = super.getHeaderNames();
        Map<String, String> allHeaders = new HashMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            allHeaders.put(headerName, super.getHeader(headerName));
        }

        // Добавляем кастомные заголовки
        allHeaders.putAll(customHeaders);

        return new Enumeration<String>() {
            private final Iterator<String> iterator = allHeaders.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }

    // Метод для добавления кастомных заголовков
    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }
}
