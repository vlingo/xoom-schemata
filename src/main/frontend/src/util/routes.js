import { mdiEllipse, mdiEllipseOutline, mdiFactory, mdiFileDocument, mdiFileDocumentOutline, mdiHome, mdiHomeOutline, mdiStore, mdiStoreOutline, mdiTag, mdiTagOutline } from '@mdi/js';

export default [
	{
		text: 'Home',
		icon: mdiHomeOutline,
		openIcon: mdiHome,
		href: ''
	},
	{
		text: 'Organization',
		icon: mdiFactory,
		openIcon: mdiFactory,
		href: 'organization'
	},
	{
		text: 'Unit',
		icon: mdiStoreOutline,
		openIcon: mdiStore,
		href: 'unit'
	},
	{
		text: 'Context',
		icon: mdiEllipseOutline,
		openIcon: mdiEllipse,
		href: 'context'
	},
	{
		text: 'Schema',
		icon: mdiFileDocumentOutline,
		openIcon: mdiFileDocument,
		href: 'schema'
	},
	{
		text: 'Schema Version',
		icon: mdiTagOutline,
		openIcon: mdiTag,
		href: 'schemaVersion'
	},
];