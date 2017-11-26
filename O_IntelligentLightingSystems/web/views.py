"""Bajta hack views module."""

import flask

from web.butler import butler


current_light = {
    "value": 1,
    "id": 1,
    "mode": "manual",
}


def init_views(app):
    """Initialize all views.

    Views should be initialized in a function to prevent dependencies between
    module imports.

    Args:
        app: Flask App.
    """
    # pylint: disable=unused-variable
    @app.route('/')
    def index():
        return flask.render_template("app.html")

    @app.route('/status')
    def status():
        status = butler.get_light_status()

        if current_light['value'] != status:
            current_light['mode'] = 'manual'

        if current_light['mode'] == 'auto':
            ps = butler.get_pir_status()
            if ps != status:
                butler.switch_light_relay()
            status = butler.get_light_status()

        current_light['value'] = status

        test_cases = [{
            "value": bool(current_light['value']),
            "id": 1,
            "mode": current_light['mode'],
        }]
        response = app.response_class(
            response=flask.json.dumps(test_cases),
            status=200,
            mimetype='application/json'
        )
        return response

    @app.route('/mode/<int:post_id>', methods=['POST'])
    def show_post(post_id):
        # show the post with the given id, the id is an integer
        value = int(flask.request.form['value'] == 'true')
        mode = flask.request.form['mode']
        current_light['mode'] = mode
        status = butler.get_light_status()
        if mode != 'auto' and status != value:
            butler.switch_light_relay()
        return "Ok"
