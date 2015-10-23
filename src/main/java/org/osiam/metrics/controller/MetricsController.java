/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.metrics.controller;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.json.MetricsModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * {@link MetricsController} with just one action to get metrics data
 *
 */
@Controller
@RequestMapping(value = "/Metrics")
public class MetricsController {

    private static final String RATE_UNIT = MetricsController.class.getCanonicalName() + ".rateUnit";
    private static final String DURATION_UNIT = MetricsController.class.getCanonicalName() + ".durationUnit";

    @Autowired
    private MetricRegistry registry;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getMetrics() throws JsonProcessingException {
        String jsonResponse = createMetricsJSONMapper().writerWithDefaultPrettyPrinter().writeValueAsString(registry);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    private ObjectMapper createMetricsJSONMapper() {
        final TimeUnit rateUnit = parseTimeUnit(RATE_UNIT, TimeUnit.SECONDS);
        final TimeUnit durationUnit = parseTimeUnit(DURATION_UNIT, TimeUnit.SECONDS);
        return new ObjectMapper().registerModule(new MetricsModule(rateUnit, durationUnit, false));
    }

    private TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
        try {
            return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
