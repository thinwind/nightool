function async(arg, callback) {
    console.log('参数为 ' + arg + ' , 1秒后返回结果');
    setTimeout(function () { callback(arg * 2); }, 1000);
}

function final(value) {
    console.log('完成: ', value);
}

var items = [1, 2, 3, 4, 5, 6];
var results = [];

function series(item,lastResult) {
    if (item) {
        async(lastResult, function (result) {
            results.push(result);
            return series(items.shift(),result);
        });
    } else {
        return final(results[results.length - 1]);
    }
}

series(items.shift(),1);