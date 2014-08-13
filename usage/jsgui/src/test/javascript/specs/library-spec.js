/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
/**
 * Test for availability of JavaScript functions/libraries.
 * We test for a certain version. People need to be aware they can break stuff when upgrading.
 * TODO: find a way to test bootstrap.js version ?
 */

define([
    'underscore', 'jquery', 'backbone', 'formatJson'
], function (_, $, Backbone, formatJson) {

    describe('Test the libraries', function () {

        describe('underscore.js', function () {
            var version = '1.4.4';
            it('must be version ' + version, function () {
                expect(_.VERSION).toEqual(version)
            })
        })

        describe('jquery', function () {
            var version = '1.9.1';
            it('must be version ' + version, function () {
                expect(jQuery.fn.jquery).toEqual(version);
                expect(jQuery).toEqual($);
            })
        })

        describe('json-formatter', function () {
            it ('must be able to format a JSON', function () {
                expect(formatJson({ test:'twest'}).length).toEqual(23)
            });
        })

        describe('backbone', function () {
            var version = '1.0.0';
            it('must be version ' + version, function () {
                expect(Backbone.VERSION).toEqual(version)
            })
        })
    })
})