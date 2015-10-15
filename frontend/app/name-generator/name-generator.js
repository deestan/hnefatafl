'use strict';

angular.module('myApp.nameGenerator', [])
  .factory('nameGenerator', function() {
    var adjectives = "big tiny cute smoking magical sneaky lovely active mighty fast".split(" ");
    var colors = "green red yellow blue purple pink orange cyan shiny black white".split(" ");
    var subjects = "orange car computer pony unicorn bunny sneak plant flower coffee dog cat ship rocket".split(" ");

    function randomOf(list) {
      var idx = (Math.random() * list.length >> 0) % list.length;
      return list[idx];
    }

    return function generate() {
      return randomOf(adjectives) + " " + randomOf(colors) + " " + randomOf(subjects);
    }
  });
