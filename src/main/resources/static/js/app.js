/* global google, M */

function showAddEditModal(entityName, entityObject = false, imageUrl = false, subModal = false, extraObject = false) {
    if (!entityObject) {
        if ($("#id").length) {
            $("#id").val("-1").trigger('change');
        }
    }
    for (var propertyName in entityObject) {
        if ($("#" + propertyName).length) {
            $("input[name='" + propertyName + "']").each(function () {
                $(this).val(entityObject[propertyName]).trigger('change');
            });
            $("#" + propertyName).val(entityObject[propertyName]).trigger('change');
            if ($("#" + propertyName).val() === null) {
                window["temp_" + propertyName] = entityObject[propertyName];
            }
        }
    }
    for (var propertyName in extraObject) {
        if ($("#" + propertyName).length) {
            $("input[name='" + propertyName + "']").each(function () {
                $(this).val(extraObject[propertyName]).trigger('change');
            });
            $("#" + propertyName).val(extraObject[propertyName]).trigger('change');
            if ($("#" + propertyName).val() === null) {
                window["temp_" + propertyName] = extraObject[propertyName];
            }
        }
    }

    if (imageUrl && $('#fantasticPicturePreview').length) {
        $('#fantasticPicturePreview').attr('src', imageUrl).fadeIn('slow');
    }

    if (!entityObject) {
        $(subModal ? ("#" + subModal + "_form") : "#add_edit_form").find("input:text, input:password, input:file, select, textarea").val('').trigger('change');
        $(subModal ? ("#" + subModal + "_form") : "#add_edit_form").find("input:radio, input.checkbox").removeAttr('checked').removeAttr('selected');
    }
    $("textarea").each(function () {
        M.textareaAutoResize(this);
    });
    $("select").formSelect();
    $(subModal ? ("#" + subModal + "_button") : "#add_edit_button").html(entityObject ? "<i class='material-icons left'>edit</i> Edit" : "<i class='material-icons left'>add</i> Add");
    $(subModal ? ("#" + subModal + "_label") : "#add_edit_label").html((entityObject ? "Edit" : "Add") + " " + entityName);
    var instance = M.Modal.getInstance(document.getElementById(subModal ? (subModal + "_modal") : 'add_edit_modal'));
    instance.open();
}

function showConfirmModal(entityName, action, link) {
    $("#confirm_modal_message").html('Are you sure you want to <b>' + action + '</b> this <b>' + entityName + '</b> ?');
    $("#confirm_modal_link").attr("href", link);
    var instance = M.Modal.getInstance(document.getElementById('confirm_modal'));
    instance.open();
}


function load(url, data, section) {
    $("#" + section).html(
            '<br/><br/><br/><div class="col s12 preloader-wrapper center-align center big active">' +
            '<div class="spinner-layer spinner-blue-only">' +
            '<div class="circle-clipper left">' +
            '<div class="circle"></div>' +
            '</div>' +
            '<div class="gap-patch">' +
            '<div class="circle"></div>' +
            '</div>' +
            '<div class="circle-clipper right">' +
            '<div class="circle"></div>' +
            '</div>' +
            '</div>' +
            '</div>');
    if ($('#' + section + '_link').length)
        $('#' + section + '_link').click();
    $.ajax({
        url: url.split("_").join("/"),
        data: data,
        type: "POST",
        success: function (data) {
            $("#" + section).html(data);
            if ($('#' + section + '_link'))
                $('#' + section + '_link').click();
        }
    }).fail(function () {
        showToast("Network Error", "red", "info");
        $("#" + section).html('<br/><br/><br/><div class="col s12 center-align"><i class="red-text material-icons large">error</i><br/> Network Error !!!</div>');
        if ($('#' + section + '_link'))
            $('#' + section + '_link').click();
    });
}

function initNavLink(link) {
    if ($("#" + link).length) {
        $("#" + link + "_link").click(function () {
            updateLink(link);
            load(link, {'embed': 'yes'}, "content");
        });
    }
}

function updateLink(link) {
    if ($("#" + current).length) {
        $("#" + current).removeClass('active');
    }
    if ($("#" + link).length) {
        $("#" + link).addClass('active');
    }
    current = link;
    window.history.pushState(null, null, link.split("_").join("/"));
}

function showToast(text, color, icon, ok = false) {
    if (text === null || text === '' || text === "null")
        return;
    var toastHTML = '<i class="' + color + '-text material-icons">' + icon + '</i>' +
            '<span>' + text + '</span>';
    if (ok) {
        toastHTML += '<button class="btn-flat purple-text toast-action">OK</button>';
    }
    M.toast({html: toastHTML});
}

function initMapWithAutoComplete(latitude = 9.0338725, longitude = 8.677457, map_div = "map") {
    var map = new google.maps.Map(document.getElementById(map_div), {
        center: {
            lat: latitude,
            lng: longitude
        },
        zoom: (latitude === 9.0338725 && longitude === 8.677457) ? 6 : 18,
        mapTypeId: google.maps.MapTypeId.HYBRID,
        tilt: 45
    });
    var card = document.getElementById('pac-card');
    var input = document.getElementById('pac-input');
    map.controls[google.maps.ControlPosition.TOP_RIGHT].push(card);
    var autocomplete = new google.maps.places.Autocomplete(input);
    // Bind the map's bounds (viewport) property to the autocomplete object,
    // so that the autocomplete requests use the current map bounds for the
    // bounds option in the request.
    autocomplete.bindTo('bounds', map);
    autocomplete.setComponentRestrictions(
            {'country': ['ng']});
    autocomplete.setTypes(['address']);
    // Set the data fields to return when the user selects a place.
    autocomplete.setFields(
            ['address_components', 'geometry', 'icon', 'name']);
    var infowindow = new google.maps.InfoWindow();
    var infowindowContent = document.getElementById('infowindow-content');
    infowindow.setContent(infowindowContent);
    var marker = new google.maps.Marker({
        map: map,
        anchorPoint: new google.maps.Point(0, -29),
        mapTypeId: google.maps.MapTypeId.HYBRID,
        tilt: 45
    });
    autocomplete.addListener('place_changed', function () {
        infowindow.close();
        marker.setVisible(false);
        var place = autocomplete.getPlace();
        if (!place.geometry) {
            // User entered the name of a Place that was not suggested and
            // pressed the Enter key, or the Place Details request failed.
            window.alert("No details available for input: '" + place.name + "'");
            return;
        }

        // If the place has a geometry, then present it on a map.
        if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
        } else {
            map.setCenter(place.geometry.location);
            map.setZoom(18); // Why 17? Because it looks good.
        }
        $('#latitude').val(place.geometry.location.lat);
        $('#longitude').val(place.geometry.location.lng);
        marker.setPosition(place.geometry.location);
        marker.setVisible(true);
        var address = '';
        if (place.address_components) {
            address = [
                (place.address_components[0] && place.address_components[0].short_name || ''),
                (place.address_components[1] && place.address_components[1].short_name || ''),
                (place.address_components[2] && place.address_components[2].short_name || '')
            ].join(' ');
        }

        infowindowContent.children['place-icon'].src = place.icon;
        infowindowContent.children['place-name'].textContent = place.name;
        infowindowContent.children['place-address'].textContent = address;
        infowindow.open(map, marker);
    });
    return map;
}

function initMap(map_div = "map", latitude = 9.0338725, longitude = 8.677457) {
    var myLatlng = new google.maps.LatLng(latitude, longitude);
    var map = new google.maps.Map(document.getElementById(map_div), {
        center: myLatlng,
        zoom: 18,
        mapTypeId: google.maps.MapTypeId.HYBRID,
        tilt: 45
    });
    var marker = new google.maps.Marker({
        map: map,
        position: myLatlng,
        mapTypeId: google.maps.MapTypeId.HYBRID,
        tilt: 45
    });
    marker.setMap(map);
    return map;
}

function readImageURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#fantasticPicturePreview').attr('src', e.target.result).fadeIn('slow');
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function windowResize() {
    ($(window).width() > 990) ? $("#main, #sidenav").each(function () {
        $(this).css({
            "padding-left": "300px"
        });
    }) : $("#main, #sidenav").each(function () {
        $(this).css({
            "padding-left": "0px"
        });
    });
}