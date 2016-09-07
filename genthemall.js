var FS = require('fs');
var LWIP = require('lwip');
var _ = require('lodash');

var fish = ["carp", "trout", "koi", "steed", "dace", "catfish", "chub", "bitterling", "loach", "bluegill", "bass", "snakehead", "eel", "goby", "smelt", "char", "stringfish", "goldfish", "guppy", "angelfish", "piranha", "arowana", "arapaima", "crawfish", "frog", "killifish", "jellyfish", "snapper", "knifejaw"];
var prettyFormatted = "";
for (let i = 0; i < fish.length; i++) {
    let a = fish[i];
    FS.writeFileSync("models/item/" + a + ".json", JSON.stringify({
        "parent": "item/generated",
        "textures": {
            "layer0": "mfish:items/fish_" + a + "_raw"
        },
        "display": {
            "head": {
                "rotation": [0, 90, -60],
                "translation": [-7, -4, -7],
                "scale": [0.8, 0.8, 0.8]
            }
        }
    }, null, 2));

    FS.writeFileSync("models/item/cooked_" + a + ".json", JSON.stringify({
        "parent": "item/generated",
        "textures": {
            "layer0": "mfish:items/fish_" + a + "_cooked"
        },
    }, null, 2));

    LWIP.open('fish_cod_cooked.png', function (err, image) {
        if (err) return console.error(err);
        image.batch()
            .hue(i * 10)
            .saturate(i / 10)
            .writeFile('textures/items/fish_' + a + "_cooked.png", function (err) {
                if (err) return console.error(err);
            });
    });

    LWIP.open('fish_cod_raw.png', function (err, image) {
        if (err) return console.error(err);
        image.batch()
            .hue(i * 10)
            .saturate(i / 10)
            .writeFile('textures/items/fish_' + a + "_raw.png", function (err) {
                if (err) return console.error(err);
            });
    });
    // prettyFormatted += "item.mfish_fish_" + a + "_raw.name=" + _.capitalize(a) + "\n";
    prettyFormatted += "item.mfish_fish_" + a + "_cooked.name=Cooked " + _.capitalize(a) + "\n";
}
FS.writeFileSync("lang/en_US.lang", prettyFormatted);